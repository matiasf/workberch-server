package filter;

import play.api.libs.iteratee.Iteratee;
import play.api.mvc.EssentialAction;
import play.api.mvc.EssentialFilter;
import play.api.mvc.RequestHeader;
import play.api.mvc.Result;
import scala.Function1;

public class T2flowXmlFilter implements EssentialFilter {

	@Override
	public EssentialAction apply(final EssentialAction next) {

		return new EssentialAction() {

			@Override
			public Iteratee<byte[], Result> apply(final RequestHeader request) {
				return next.apply(request);
			}

			@Override
			public <A> Function1<A, Iteratee<byte[], Result>> compose(final Function1<A, RequestHeader> arg0) {
				return next.compose(arg0);
			}

			@Override
			public boolean apply$mcZJ$sp(final long arg0) {
				return next.apply$mcZJ$sp(arg0);
			}

			@Override
			public boolean apply$mcZI$sp(final int arg0) {
				return next.apply$mcZI$sp(arg0);
			}

			@Override
			public boolean apply$mcZF$sp(final float arg0) {
				return next.apply$mcZF$sp(arg0);
			}

			@Override
			public boolean apply$mcZD$sp(final double arg0) {
				return next.apply$mcZD$sp(arg0);
			}

			@Override
			public void apply$mcVJ$sp(final long arg0) {
				next.apply$mcVJ$sp(arg0);
			}

			@Override
			public void apply$mcVI$sp(final int arg0) {
				next.apply$mcVI$sp(arg0);
			}

			@Override
			public void apply$mcVF$sp(final float arg0) {
				next.apply$mcVF$sp(arg0);
			}

			@Override
			public void apply$mcVD$sp(final double arg0) {
				next.apply$mcVD$sp(arg0);
			}

			@Override
			public long apply$mcJJ$sp(final long arg0) {
				return next.apply$mcJJ$sp(arg0);
			}

			@Override
			public long apply$mcJI$sp(final int arg0) {
				return next.apply$mcJI$sp(arg0);
			}

			@Override
			public long apply$mcJF$sp(final float arg0) {
				return next.apply$mcJD$sp(arg0);
			}

			@Override
			public long apply$mcJD$sp(final double arg0) {
				return next.apply$mcJD$sp(arg0);
			}

			@Override
			public int apply$mcIJ$sp(final long arg0) {
				return next.apply$mcIJ$sp(arg0);
			}

			@Override
			public int apply$mcII$sp(final int arg0) {
				return next.apply$mcII$sp(arg0);
			}

			@Override
			public int apply$mcIF$sp(final float arg0) {
				return next.apply$mcIF$sp(arg0);
			}

			@Override
			public int apply$mcID$sp(final double arg0) {
				return next.apply$mcID$sp(arg0);
			}

			@Override
			public float apply$mcFJ$sp(final long arg0) {
				return next.apply$mcFJ$sp(arg0);
			}

			@Override
			public float apply$mcFI$sp(final int arg0) {
				return next.apply$mcFI$sp(arg0);
			}

			@Override
			public float apply$mcFF$sp(final float arg0) {
				return next.apply$mcFF$sp(arg0);
			}

			@Override
			public float apply$mcFD$sp(final double arg0) {
				return next.apply$mcFD$sp(arg0);
			}

			@Override
			public double apply$mcDJ$sp(final long arg0) {
				return next.apply$mcDJ$sp(arg0);
			}

			@Override
			public double apply$mcDI$sp(final int arg0) {
				return next.apply$mcDI$sp(arg0);
			}

			@Override
			public double apply$mcDF$sp(final float arg0) {
				return next.apply$mcDF$sp(arg0);
			}

			@Override
			public double apply$mcDD$sp(final double arg0) {
				return next.apply$mcDD$sp(arg0);
			}

			@Override
			public <A> Function1<RequestHeader, A> andThen(final Function1<Iteratee<byte[], Result>, A> arg0) {
				return next.andThen(arg0);
			}

			@Override
			public EssentialAction apply() {
				return next.apply();
			}
		};
	}

}
