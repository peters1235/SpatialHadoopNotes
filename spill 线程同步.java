init  
      spillLock.lock();
      try {
        spillThread.start();
        while (!spillThreadRunning) {
          spillDone.await();
        }
     } finally {
        spillLock.unlock();
      

protected class SpillThread extends Thread {

    @Override
    public void run() {
      spillLock.lock();
      spillThreadRunning = true;
      try {
        while (true) {
          spillDone.signal();
          while (!spillInProgress) {
            spillReady.await();
          }
          try {
            spillLock.unlock();
            sortAndSpill();
          } catch (Throwable t) {
            sortSpillException = t;
          } finally {
            spillLock.lock();
            if (bufend < bufstart) {
              bufvoid = kvbuffer.length;
            }
            kvstart = kvend;
            bufstart = bufend;
            spillInProgress = false;
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        spillLock.unlock();
        spillThreadRunning = false;
      }
    }
  }