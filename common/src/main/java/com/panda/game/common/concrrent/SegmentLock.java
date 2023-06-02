package com.panda.game.common.concrrent;

import com.panda.game.common.utils.MixUtil;

public class SegmentLock {
	
	private Object[] locks = null;
	
	private int length;

	public SegmentLock(int size) {
		if (size <= 1 || size > (1 << 31)) {
			throw new RuntimeException("SegmentLock size error:" + size);
		}
		
		this.length = MixUtil.getBinaryNumGreatOrEquipThan(size);
		this.locks = new Object[this.length];
		for (int i = 0; i < this.length; i++) {
			this.locks[i] = new Object();
		}
	}
	
	/**
	 * 获取一把锁
	 * @param id
	 * @return
	 */
	public Object getLock(int id) {
		return this.locks[ id & (this.length - 1) ];
	}
	
	/**
	 * 获取一把锁
	 * @param id
	 * @return
	 */
	public Object getLock(long id) {
		return this.locks[ (int) (id & (this.length - 1)) ];
	}
	
	/**
	 * 适用于二进制后N位大部分一样的id
	 * @param id
	 * @return
	 */
	public Object getDiscreteLock(int id) {
		return this.locks[ MixUtil.calcMod(id, 31) & (this.length - 1) ];
	}
	
	/**
	 * 适用于二进制后N位大部分一样的id
	 * @param id
	 * @return
	 */
	public Object getDiscreteLock(long id) {
		return this.locks[ MixUtil.calcMod(id, 31) & (this.length - 1) ];
	}
	
}
