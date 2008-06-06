/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.tdb.bplustree;

import static com.hp.hpl.jena.tdb.base.block.BlockType.BTREE_BRANCH;
import static com.hp.hpl.jena.tdb.base.block.BlockType.RECORD_BLOCK;

import java.nio.ByteBuffer;

import com.hp.hpl.jena.tdb.base.block.BlockConverter;
import com.hp.hpl.jena.tdb.base.block.BlockMgr;
import com.hp.hpl.jena.tdb.base.block.BlockType;
import com.hp.hpl.jena.tdb.base.buffer.PtrBuffer;
import com.hp.hpl.jena.tdb.base.buffer.RecordBuffer;
import com.hp.hpl.jena.tdb.base.record.RecordFactory;
import com.hp.hpl.jena.tdb.btree.BTreeException;

/** BPlusTreePageMgr = BPlusTreeNode manager */
final class BPTreeNodeMgr
{
    private BPlusTree bpTree ;
    private BlockMgr blockMgr ;
    private RecordFactory keyFactory ;
    private Block2BTreeNode converter ;

    BPTreeNodeMgr(BPlusTree bpTree, BlockMgr blockMgr, BPlusTreeParams params)
    {
        this.bpTree = bpTree ;
        this.blockMgr = blockMgr ;
        this.keyFactory = params.getKeyFactory() ;
        this.converter = new Block2BTreeNode() ;
    }
   
    public BlockMgr getBlockMgr() { return blockMgr ; } 
    
    /** Allocate an uninitialized slot.  Fill with a .put later */ 
    public int allocateId()           { return blockMgr.allocateId() ; }
    
    /** Allocate root node space. The root is a node with a Records block.*/ 
    public BPTreeNode createRoot()
    { 
        BPTreeNode n = createNode(BPlusTreeParams.RootParent) ;
        // Create an empty records block.
        int recId = bpTree.getRecordsMgr().allocateId() ;
        BPTreePage page = bpTree.getRecordsMgr().create(recId) ;
        page.put();
        
        n.ptrs.add(0) ;
        n.isLeaf = true ;
        n.setCount(0) ;     // Count is count of records = ptr count -1 if not empty. 
        n.put();
        return n ;
    }
    
//    /** Allocate space for a leaf node. */
//    public BPTreeLeaf createLeaf(int parent)
//    {
//        int id = btree.getRecordPageMgr().allocateId() ;
//        RecordBufferPage page = btree.getRecordPageMgr().create(id) ;
//        BPTreeLeaf leaf = new BPTreeLeaf(btree, page) ;
//        return leaf ;
//    }
    
    /** Allocate space for a fresh node. */ 
    public BPTreeNode createNode(int parent)
    { 
        int id = blockMgr.allocateId() ;
        ByteBuffer bb = blockMgr.allocateBuffer(id) ;
        BPTreeNode n = converter.createFromByteBuffer(bb, BTREE_BRANCH) ;
        n.setId(id) ;
        n.isLeaf = false ;
        n.parent = parent ;
        return n ;
    }

    /** Fetch a block for the root. s*/
    public BPTreeNode getRoot(int id)
    {
        return get(id, BPlusTreeParams.RootParent) ;
    }
    
    /** Fetch a block */
    public BPTreeNode get(int id, int parent) { return _get(id, parent, true) ; } 
    
    /** Fetch a block */
    public BPTreeNode getSilent(int id, int parent) { return _get(id, parent, false) ; }
    
    private BPTreeNode _get(int id, int parent, boolean logged)
    {
        ByteBuffer bb = (logged) ? blockMgr.get(id) :  blockMgr.getSilent(id) ;
//        BTreeNode n = wrapExisting(btree, id, bb, parent) ;
//        return n ;
        
        BPTreeNode n = converter.fromByteBuffer(bb) ;
        n.setId(id) ;
        n.parent = parent ;
        return n ;
    }
    

    public void put(BPTreeNode node)
    {
        // ByteBuffer bb = node.getByteBuffer() ;
        ByteBuffer bb = converter.toByteBuffer(node) ;
        blockMgr.put(node.getId(), bb) ;
    }

    public void release(int id)     { blockMgr.release(id) ; }
    
    public boolean valid(int id)    { return blockMgr.valid(id) ; }
    
    public void dump()
    { 
        for ( int idx = 0 ; valid(idx) ; idx++ )
        {
            BPTreeNode n = get(idx, BPlusTreeParams.NoParent) ;
            System.out.println(n) ;
        }
    }
    
    /** Signal the start of an update operation */
    public void startUpdate()       { blockMgr.startUpdate() ; }
    
    /** Signal the completion of an update operation */
    public void finishUpdate()      { blockMgr.finishUpdate() ; }

    /** Signal the start of an update operation */
    public void startRead()         { blockMgr.startRead() ; }
    
    /** Signal the completeion of an update operation */
    public void finishRead()        { blockMgr.finishRead() ; }
    
    // ---- On-disk support
    
    // Using a BlockConverter interally.
    
    private class Block2BTreeNode implements BlockConverter.Converter<BPTreeNode>
    {
        @Override
        public BPTreeNode createFromByteBuffer(ByteBuffer bb, BlockType bType)
        { 
            return overlay(bpTree, bb, bType==RECORD_BLOCK, 0) ;
        }

        @Override
        public BPTreeNode fromByteBuffer(ByteBuffer bb)
        {
            int x = bb.getInt(0) ;
            BlockType type = getType(x) ;

            if ( type != BlockType.BTREE_BRANCH && type != BlockType.RECORD_BLOCK )
                throw new BTreeException("Wrong block type: "+type) ; 
            int count = decCount(x) ;
            return overlay(bpTree, bb, (type==BlockType.RECORD_BLOCK), count) ;
        }

        @Override
        public ByteBuffer toByteBuffer(BPTreeNode node)
        {
            // It's manipulated in-place so no conversion needed, 
            // Just the count needs to be fixed up. 
            ByteBuffer bb = node.getByteBuffer() ;
            BlockType bType = (node.isLeaf ? RECORD_BLOCK : BTREE_BRANCH ) ;
            int c = encCount(bType, node.getCount()) ;
            bb.putInt(0, c) ;
            return bb ;
        }
    }
    
//    // Leaves have a count of -(count+1)
//    // (same as the binary search encoding of "not found")
//    private static final int encCount(int i)     { return -(i+1) ; } 
//    private static final int decCount(int i)     { return -i-1 ; }

    // ----
    private static final BlockType getType(int x)
    {
        return BlockType.extract( x>>>24 ) ;
    }
    
    private static final int encCount(BlockType type, int i)
    {
        return (type.id()<<24) | (i&0x00FFFFFF) ;
    }
    
    private static final int decCount(int i)
    { 
        return i & 0x00FFFFFF ;
    }
    
    /** byte[] layout.
     * 
     * New:
     *  0: Block type
     *  1-3: Count 
     *      For an internal node, it is the number of pointers
     *      For a leaf node, it is the number of records.
     *  Leaves:
     *     4- :  Records (count of them)
     *  Internal nodes:
     *    4-X:        Records: btree.MaxRec*record length
     *    X- :        Pointers: btree*MaxPtr*ptr length 

     * OLD    
     *    0-3:        Header: Number in use.
     *      Negative (as -(i+1)implies a leaf.
     *** Change: 8 bytes (=>64bit aligned?).  Include a "block type"
     *** Or pack 8/24.
     * Leaf:
     *    4-       Records: btree.NumRec* 
     * Non-leaf:
     *    4-X:        Records: btree.MaxRec*record length
     *    X- :        Pointers: btree*MaxPtr*ptr length 
     */
    // Produce a BTreeNode from a ByteBuffer
    private static BPTreeNode overlay(BPlusTree bTree, ByteBuffer byteBuffer, boolean asLeaf, int count)
    {
//        if ( byteBuffer.order() != Const.NetworkOrder )
//            throw new BTreeException("ByteBuffer in wrong order") ;

        // Fix up the id later.
        BPTreeNode n = new BPTreeNode(bTree, -1, byteBuffer) ;
        // The count is zero at the root only.
        // When the root is zero, it's a leaf.
        formatBTreeNode(n, bTree, byteBuffer, asLeaf, count) ; 
        return n ;
    }
        
    static BPTreeNode formatBTreeNode(BPTreeNode n, BPlusTree bTree, ByteBuffer byteBuffer, boolean leaf, int count)
    {
        BPlusTreeParams params = bTree.getParams() ;

        int ptrBuffLen = params.MaxPtr * BPlusTreeParams.PtrLength ;
        int recBuffLen = params.MaxRec * params.getRecordLength() ;

//      if ( (ptrBuffLen+recBuffLen+BTreeParams.BlockHeaderSize) > n.byteBuffer.capacity() )
//      {
//      int x = (ptrBuffLen+recBuffLen+4) ;
//      throw new BTreeException(format("Short byte block: expected=%d, actual=%d", x, n.byteBuffer.capacity())) ;
//      }

        n.setId(-1) ;
        n.parent = -2 ;
        n.setCount(count) ;
        n.isLeaf = leaf ; 

        int header = BPlusTreeParams.BlockHeaderSize ;
        int rStart = header ;
        int pStart =  header+recBuffLen ;

        // Find the number of pointers.
        int numPtrs = -1 ;
            
        if ( n.getCount() < 0 )
        {
            numPtrs = 0 ;
            n.setCount(decCount(n.getCount())) ; 
        }
        else if ( n.getCount() == 0 )    // The root.
        {
            numPtrs = 0 ;
        }
        else    // Count > 0
            numPtrs = n.getCount()+1 ;

        n.getByteBuffer().position(rStart) ;
        n.getByteBuffer().limit(rStart+recBuffLen) ;
        ByteBuffer bbr = n.getByteBuffer().slice() ;
        //bbr.limit(recBuffLen) ;
        n.records = new RecordBuffer(bbr, n.params.keyFactory, n.getCount()) ;

//        if ( n.isLeaf )
//        {
//            n.ptrs = null ;
//        }
//        else
        {
            n.getByteBuffer().position(pStart) ;
            n.getByteBuffer().limit(pStart+ptrBuffLen) ;
            
            ByteBuffer bbi = n.getByteBuffer().slice() ;
            //bbi.limit(ptrBuffLen) ;
            n.ptrs = new PtrBuffer(bbi, numPtrs) ;
        }
        
        n.getByteBuffer().rewind() ;
        return n ;
    }
    
    static final void formatForRoot(BPTreeNode n, boolean asLeaf)
    {
        BPTreeNodeMgr.formatBTreeNode(n, n.bpTree, n.getByteBuffer(), asLeaf, 0) ;
        // Tweak for the root-specials.  The node is not consistent yet.
        n.setId(0) ;
        n.parent = BPlusTreeParams.RootParent ;
    }
    
}

/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */