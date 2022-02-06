import hu.kristall.rpg.Server;
import hu.kristall.rpg.Synchronizer;

public class Main {
	
	public static void main(String[] args)  {
		Synchronizer<Server> s = Server.createServer();
		
		/*Javalin j = Javalin.create(a -> {
			a.showJavalinBanner = false;
		});
		j.ws("/", ws -> {
			ws.onMessage(msg -> {
				System.out.println("message "+msg.message() +' '+ Thread.currentThread().getName());
				Thread.sleep(1000);
				System.out.println("message processed");
			});
			ws.onConnect(wsConnectContext -> {
				System.out.println("connect "+ Thread.currentThread().getName());
				Thread.sleep(5000);
				System.out.println("connect done");
			});
			ws.onClose(wsCloseContext -> {
				System.out.println("close "+ Thread.currentThread().getName());
				try {
					wsCloseContext.send("sadge :/");
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			});
			ws.onError(err -> {
				System.out.println("err "+ Thread.currentThread().getName());
			});
		});
		j.start(8080);*/
		
	}
	
}
