
synchronize 同步进行

synchronized 是Java语言关键字，当它用来修饰一个方法或者一个代码块的时候，能够保证在同一时刻最多只有一个线程执行该段代码。synchronized 关键字，它包括两种用法：synchronized 方法和 synchronized 块。  



/**  
     * 当一个线程访问object的一个synchronized(this)同步代码块时，  
     * 它就获得了这个object的对象锁。  
     * 结果，其它线程对该object对象所有同步代码部分的访问都被暂时阻塞。  
     */  
    public synchronized void method3(){  
        int k=0;  
        while(k++ < 3){  
            System.out.println(Thread.currentThread().getName() +":"+ k);  
            try {  
                Thread.sleep(1000);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
    }  