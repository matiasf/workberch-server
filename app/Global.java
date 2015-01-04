

import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import filter.T2flowXmlFilter;


public class Global extends GlobalSettings {

	@Override
	public void onStart(final Application app) {
    }

    @Override
	public void onStop(final Application app) {
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[] { T2flowXmlFilter.class  };
    }
}
