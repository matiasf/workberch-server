package filter;

import play.Logger;
import play.api.libs.iteratee.Iteratee;
import play.api.mvc.EssentialAction;
import play.api.mvc.EssentialFilter;
import play.api.mvc.RequestHeader;
import play.api.mvc.Result;
import scala.Function1;

public class T2flowXmlFilter implements EssentialFilter {

	@Override
	public EssentialAction apply(final EssentialAction next) {
		// TODO Auto-generated method stub
		Logger.debug("T2flowXml filter");
		return new EssentialAction() {
			
			@Override
			public Iteratee<byte[], Result> apply(RequestHeader request) {
				
				if (request.headers().keys().contains("ContentType")) {
					if (request.headers().get("ContentType").get().equals("application/vnd.taverna.t2flow+xml")) {
						Logger.debug("SIIIII application/vnd.taverna.t2flow+xml");
						
						
				    }
					Logger.debug("ContentType:" + request.headers().get("ContentType").get().toString());
				}
					
					
					
				return next.apply(request);
			}
			
			@Override
			public <A> Function1<A, Iteratee<byte[], Result>> compose(
					Function1<A, RequestHeader> arg0) {
				return next.compose(arg0);
			}
			
			@Override
			public boolean apply$mcZJ$sp(long arg0) {
				return next.apply$mcZJ$sp(arg0);
			}
			
			@Override
			public boolean apply$mcZI$sp(int arg0) {
				return next.apply$mcZI$sp(arg0);
			}
			
			@Override
			public boolean apply$mcZF$sp(float arg0) {
				return next.apply$mcZF$sp(arg0);
			}
			
			@Override
			public boolean apply$mcZD$sp(double arg0) {
				return next.apply$mcZD$sp(arg0);
			}
			
			@Override
			public void apply$mcVJ$sp(long arg0) {
				next.apply$mcVJ$sp(arg0);
			}
			
			@Override
			public void apply$mcVI$sp(int arg0) {
				next.apply$mcVI$sp(arg0);
			}
			
			@Override
			public void apply$mcVF$sp(float arg0) {
				next.apply$mcVF$sp(arg0);
			}
			
			@Override
			public void apply$mcVD$sp(double arg0) {
				next.apply$mcVD$sp(arg0);
			}
			
			@Override
			public long apply$mcJJ$sp(long arg0) {
				return next.apply$mcJJ$sp(arg0);
			}
			
			@Override
			public long apply$mcJI$sp(int arg0) {
				return next.apply$mcJI$sp(arg0);
			}
			
			@Override
			public long apply$mcJF$sp(float arg0) {
				return next.apply$mcJD$sp(arg0);
			}
			
			@Override
			public long apply$mcJD$sp(double arg0) {				
				return next.apply$mcJD$sp(arg0);
			}
			
			@Override
			public int apply$mcIJ$sp(long arg0) {
				return next.apply$mcIJ$sp(arg0);
			}
			
			@Override
			public int apply$mcII$sp(int arg0) {
				return next.apply$mcII$sp(arg0);
			}
			
			@Override
			public int apply$mcIF$sp(float arg0) {
				return next.apply$mcIF$sp(arg0);
			}
			
			@Override
			public int apply$mcID$sp(double arg0) {
				return next.apply$mcID$sp(arg0);
			}
			
			@Override
			public float apply$mcFJ$sp(long arg0) {
				return next.apply$mcFJ$sp(arg0);
			}
			
			@Override
			public float apply$mcFI$sp(int arg0) {
				return next.apply$mcFI$sp(arg0);
			}
			
			@Override
			public float apply$mcFF$sp(float arg0) {
				return next.apply$mcFF$sp(arg0);
			}
			
			@Override
			public float apply$mcFD$sp(double arg0) {
				return next.apply$mcFD$sp(arg0);
			}
			
			@Override
			public double apply$mcDJ$sp(long arg0) {
				return next.apply$mcDJ$sp(arg0);
			}
			
			@Override
			public double apply$mcDI$sp(int arg0) {
				return next.apply$mcDI$sp(arg0);
			}
			
			@Override
			public double apply$mcDF$sp(float arg0) {
				return next.apply$mcDF$sp(arg0);
			}
			
			@Override
			public double apply$mcDD$sp(double arg0) {
				return next.apply$mcDD$sp(arg0);
			}
			
			@Override
			public <A> Function1<RequestHeader, A> andThen(
					Function1<Iteratee<byte[], Result>, A> arg0) {
				return next.andThen(arg0);
			}
			
			@Override
			public EssentialAction apply() {
				return next.apply();
			}
		};
		
	}
	


}

