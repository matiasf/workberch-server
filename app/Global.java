
import filter.T2flowXmlFilter;
import play.*;
import play.api.mvc.EssentialFilter;
import play.mvc.*;


public class Global extends GlobalSettings {

	public void onStart(Application app) {
        Logger.debug("Application has started");
    }

    public void onStop(Application app) {
        Logger.debug("Application shutdown...");
    }
    
    @Override
    public Action onRequest(Http.Request request, java.lang.reflect.Method actionMethod) {
       Logger.debug("before each request..." + request.toString());
       return super.onRequest(request, actionMethod);
    }
    
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[] { T2flowXmlFilter.class  };
    }
}
