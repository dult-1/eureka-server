package dult;



import org.nutz.http.Http;
import org.nutz.http.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by dult on 2021-11-9.
 */
public class Test {
    public static final Semaphore SEMAPHORE = new Semaphore(3);
    public static InheritableThreadLocal<Integer> threadLocal = new InheritableThreadLocal<>();
    public static ExecutorService service = Executors.newFixedThreadPool(5);
    public static volatile int num=0;
    public static void main(String[] args) {
        for (int i = 0; i <30 ; i++) {
            service.submit(new TestRunnable());
        }
        service.shutdown();
    }

    static class TestRunnable implements Runnable{
        @Override
        public void run() {
            try {
                SEMAPHORE.acquire();
                Map<String,Object> map = new HashMap<>();
                int resultNum = getNum();
                map.put("id",resultNum);
                System.out.println("=id==" + resultNum);
                Response response = Http.get("http://localhost:9000/ribbon-consumer-find",map,5000);
                System.out.println("===" + response.getContent());
                SEMAPHORE.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized int getNum(){
            return num++;
        }
    }
}
