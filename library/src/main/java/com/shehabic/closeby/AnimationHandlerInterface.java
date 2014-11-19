package com.shehabic.closeby;

import android.view.View;

public interface AnimationHandlerInterface
{
	public void show(View helpIcon, CloseBy closeByLib);
	public void hide(View helpIcon, CloseBy closeByLib);
	public boolean animateHide();
	public boolean animateShow();
}
