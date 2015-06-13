package tw.com.ehanlin.mde.util;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataStack {

    public DataStack(DataStack parent, Object self){
        this.parent = parent;
        this.self = self;
    }

    public DataStack getParent() {
        return parent;
    }

    public void setParent(DataStack parent) {
        this.parent = parent;
    }

    public Object getSelf() {
        return self;
    }

    public void setSelf(Object self) {
        this.self = self;
    }

    public Boolean hasParent() {
        return getParent() != null;
    }

    protected DataStack parent;
    protected Object self;

    public static class ConcurrentDataStack extends DataStack {

        public ConcurrentDataStack(DataStack parent, Object self) {
            super(parent, self);
        }

        @Override
        public DataStack getParent() {
            parentLock.readLock().lock();
            try{
                return super.getParent();
            }finally {
                parentLock.readLock().unlock();
            }
        }

        @Override
        public void setParent(DataStack parent) {
            parentLock.writeLock().lock();
            try{
                super.setParent(parent);
            }finally {
                parentLock.writeLock().unlock();
            }
        }

        @Override
        public Object getSelf() {
            selfLock.readLock().lock();
            try{
                return super.getSelf();
            }finally {
                selfLock.readLock().unlock();
            }
        }

        @Override
        public void setSelf(Object self) {
            selfLock.writeLock().lock();
            try{
                super.setSelf(self);
            }finally {
                selfLock.writeLock().unlock();
            }
        }

        private ReadWriteLock parentLock = new ReentrantReadWriteLock();
        private ReadWriteLock selfLock = new ReentrantReadWriteLock();

    }
}
