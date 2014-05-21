package com.hp.it.perf.ac.service.chain;

import java.io.Serializable;

public class AcRelation implements Serializable {
    private static final long serialVersionUID = 5663100000491190771L;

    private long acid;
    
    // used for attach underline graph object
    private transient Object attachment;

    public long getAcid() {
        return acid;
    }

    public void setAcid(long acid) {
        this.acid = acid;
    }
    
    public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (acid ^ (acid >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AcRelation other = (AcRelation) obj;
        if (acid != other.acid)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AcRelation [acid=" + acid + "]";
    }

}
