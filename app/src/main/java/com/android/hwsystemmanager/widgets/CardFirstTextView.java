package com.android.hwsystemmanager.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


/* compiled from: CardFirstTextView.kt */
/* loaded from: classes2.dex */
public final class CardFirstTextView extends TextView {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CardFirstTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setTypeface(Typeface.create(Typeface.DEFAULT, 500, false));
    }
}
