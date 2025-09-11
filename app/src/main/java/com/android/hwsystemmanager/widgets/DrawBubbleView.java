package com.android.hwsystemmanager.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;

import com.android.hwsystemmanager.BatteryStackBarData;
import com.android.hwsystemmanager.LevelAndCharge;
import com.android.hwsystemmanager.SelectedItem;
import com.android.hwsystemmanager.utils.Logcat;
import com.android.hwsystemmanager.utils.ScreenReaderUtils;
import com.android.hwsystemmanager.utils.ViewExtKt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DrawBubbleView {
    static public void drawChart(BatteryBarChart barChart, Canvas canvas) {
        int i4;
        int i8;
        int i10;
        List<BatteryStackBarData> mBarLists = barChart.mBarLists;
        Iterator<BatteryStackBarData> it2 = mBarLists.iterator();
        Path path = new Path();
        int i12 = 0;
        int i11 =0;
        boolean z11;
        boolean z12;
        boolean z13;
        boolean z14;
        boolean z15;
        boolean z16;
        while (true) {
            i4 = 1;
            i8 = 2;
            if (!it2.hasNext()) {
                break;
            }
            BatteryStackBarData next = it2.next();
            int i13 = i12 + 1;
            if (i12 >= 0) {
                if (i12 >=  barChart.f9902h) {
                    break;
                }
                boolean m10301a = TextUtils.equals(next.chargingStatus, "true");
                if (i12 == 0) {
                    int i14 = barChart.m7039f() ? 4 : 4;
                    i10 = i14;
                } else {
                    if (i12 ==  barChart.f9902h - 1) {
                        if (!barChart.m7039f()) {
                            if (barChart.getStartIndex() == i12 && barChart.getSelectedTimeSpand() == 1800000) {
                                z14 = true;
                            } else {
                                z14 = false;
                            }
                            if (barChart.getEndIndex() == i12 && barChart.getSelectedTimeSpand() == 3600000) {
                                z15 = true;
                            } else {
                                z15 = false;
                            }
                            if ( barChart.f9902h - barChart.getEndIndex() == 2) {
                                z16 = true;
                            } else {
                                z16 = false;
                            }
                            if (!z14) {
                                if (z15) {
                                }
                                if (barChart.getEndIndex() == i12 - 1) {
                                }
                            }
                            i11 = 4;
                        }
                        i10 = i11;
                    } else if (i12 < mBarLists.size() && i12 >= 1) {
                        if (barChart.m7039f()) {
                            if (i12 != mBarLists.size() - 1) {
                                if (BatteryBarChart.m7032g(i12, mBarLists) && BatteryBarChart.m7033h(i12, mBarLists)) {
                                    i8 = 4;
                                } else {
                                    if (!BatteryBarChart.m7032g(i12, mBarLists)) {
                                    }
                                    i8 = 1;
                                }
                                i10 = i8;
                            }
                        } else {
                            int startIndex = barChart.getStartIndex();
                            int endIndex = barChart.getEndIndex();
                            if ((i12 == startIndex && BatteryBarChart.m7033h(i12, mBarLists)) || (i12 == endIndex && BatteryBarChart.m7032g(i12, mBarLists))) {
                                i10 = 4;
                            } else {
                                if (i12 == barChart.getEndIndex() + 1 || i12 == barChart.getStartIndex()) {
                                    z11 = true;
                                } else {
                                    z11 = false;
                                }
                                if (!z11) {
                                    if (i12 ==  barChart.f9902h - 1 || i12 == barChart.getStartIndex() - 1) {
                                        z12 = true;
                                    } else {
                                        z12 = false;
                                    }
                                    if (z12 || i12 == barChart.getEndIndex()) {
                                        z13 = true;
                                    } else {
                                        z13 = false;
                                    }
                                }
                                i8 = 1;
                                i10 = i8;
                            }
                        }
                    } else {
                        i10 = 1;
                    }
                    i11 = 3;
                    i10 = i11;
                }
                if (i10 == 1 || i10 == 4) {
                    path = new Path();
                }
                Path path2 = path;
                if (i12 == 0 &&  barChart.f9902h > 0) {
                    next.drawBar(canvas, m10301a, next.levelAndCharge.getLevel(), path2, i10);
                }
                if (1 > i12 || i12 >=  barChart.f9902h) {
                    i4 = 0;
                }
                if (i4 != 0) {
                    next.drawCurve(canvas,/* m10301a, */ mBarLists.get(i12 - 1)/*.levelAndCharge.getLevel(), path2, i10*/);
                }
                i12 = i13;
                path = path2;
            }
        }
        drawBubbleView(barChart, canvas, i8, i4);
    }

    static public void drawBubbleView(BatteryBarChart barChart, Canvas canvas, int i8, int i4) {
        List<BatteryStackBarData> mBarLists = barChart.mBarLists;
        Iterator<BatteryStackBarData> it3 = mBarLists.iterator();
        BubbleView bubbleView;

        int i15 = 0;
        float bubbleStartX = 0;
        float m10476a = 0;
        int i9;
        float f11;
        float f12;
        float f13;
        float f14;
        float f15;
        boolean z10;
        float f16;
        float f17;
        float f18;
        float f19 = 0;
        float f20;
        float f21;
        while (it3.hasNext()) {
            BatteryStackBarData next2 = it3.next();
            int i16 = i15 + 1;
            if (i15 >= 0) {
                if (next2.showBubble && !barChart.m7039f()) {
                    int barDataState = next2.state;
                    int size = mBarLists.size() - i8;
                    LevelAndCharge levelAndCharge = next2.levelAndCharge;
                    if (i15 < size) {
                        LevelAndCharge levelAndCharge2 = mBarLists.get(i16).levelAndCharge;
                        if (levelAndCharge.getLevel() < levelAndCharge2.getLevel()) {
                            levelAndCharge = levelAndCharge2;
                        }
                    }
                    float startY = next2.y;
                    Logcat.d("BatteryBarChart", "it.y2 is " + startY);
                    Logcat.d("BatteryBarChart", "it.level2 is " + next2.percentageText);
                    float f23 = (((barChart.f9909o - barChart.f9908n) * ((float) (100 - levelAndCharge.getLevel()))) / ((float) 100)) + startY;
                    Logcat.d("BatteryBarChart", "drawBubbleView chart startY--" + startY + " , height--" + barChart.getHeight() + " , level--" + levelAndCharge.getLevel());
                    Logcat.d("BatteryBarChart", "barDataState is " + barDataState);
                    Logcat.d("BatteryBarChart", "startY is " + startY);
                    float startX = next2.x;
                    Logcat.d("BatteryBarChart", "startX is " + startX);
                    if (barDataState != -1) {
                        if (barDataState != i4) {
                            if (barDataState == 2) {
                                bubbleStartX = startX - (barChart.f9894B / 2);
                                m10476a = ViewExtKt.dp2px(barChart,3.0f);
                            }
                        } else {
                            bubbleStartX = startX + barChart.f9893A + (barChart.f9894B / 2);
                            m10476a = ViewExtKt.dp2px(barChart,3.0f);
                        }
                    } else {
                        bubbleStartX = startX + (barChart.f9893A / 2);
                        m10476a =ViewExtKt.dp2px(barChart,3.0f);
                    }
                    float bubbleStartY = f23 - m10476a;
                    Logcat.d("BatteryBarChart", "bubbleStartX is " + bubbleStartX + "    bubbleStartY is " + bubbleStartY + " ");
                    SelectedItem selectedItem = new SelectedItem(bubbleStartX, bubbleStartY, barChart.getSelectedTime(), (float) barChart.mWidth);
                    selectedItem.setState(barChart.m7037d(barChart.getStartIndex(), barChart.getEndIndex()));
                    Context context = barChart.getContext();
                    bubbleView = new BubbleView(context, selectedItem);
                    if (!ScreenReaderUtils.checkScreenReaderStatus()) {
                        Path path3 = new Path();
                        float pointY = bubbleView.mStartY;
                        Logcat.d("BubbleView", "pointY is " + pointY);
                        float f27 = bubbleView.mStartX;
                        path3.moveTo(f27, pointY);
                        float f28 = bubbleView.f22165m;
                        float f29 = f28 + f27;
                        float f30 = pointY - bubbleView.f22166n;
                        float f31 = f30 - 1.0f;
                        float f32 = bubbleView.f22157e;
                        float f33 = bubbleView.f22170r;
                        float f34 = f32 - f33;
                        float f35 = bubbleView.f22163k;
                        float f36 = bubbleView.f22155c;
                        float f37 = bubbleView.f22169q;
                        i9 = i16;
                        int i18 = bubbleView.mRadius;
                        f11 = f31;
                        f12 = f32;
                        if (f29 > f34) {
                            f14 = (f30 - (i18 / 2f)) - (f35 / 2f);
                            f13 = f12;
                        } else {
                            f13 = f37 + f33 + f36;
                            if (f29 >= f13) {
                                Logcat.d("BubbleView", "normal");
                                f13 = f29;
                            }
                            f14 = f11;
                        }
                        z10 = bubbleView.f22158f;
                        float f38 = f13;
                        int i19 = bubbleView.f22167o;
                        float f39 = f14;
                        float f40 = bubbleView.screenWidth;
                        if (z10) {
                            float f41 = f27 - f28;
                            f15 = f29;
                            if (f41 < f33 + f36) {
                                f39 = (f30 - (i18 / 2f)) - (f35 / 2f);
                                f16 = f36;
                            } else {
                                f16 = (f40 - f36) - i19;
                                if (f41 <= f16) {
                                    Logcat.d("BubbleView", "normal");
                                    f16 = f41;
                                }
                                f39 = f11;
                            }
                        } else {
                            f15 = f29;
                            f16 = f38;
                        }
                        path3.lineTo(f16, f39);
                        float f42 = f27 - f28;
                        if (f42 < f37 + f33 + f36) {
                            f42 = f37 + f36;
                            f17 = (f30 - (i18 / 2f)) - (f35 / 2);
                        } else {
                            float f43 = (f12 - f37) - f28;
                            if (f42 > f43) {
                                f42 = f43;
                            } else {
                                Logcat.d("BubbleView", "normal");
                            }
                            f17 = f11;
                        }
                        if (z10) {
                            if (f15 > ((f40 - f36) - f37) - f33) {
                                f42 = f12 - f37;
                                f18 = (f30 - (i18 / 2f)) - (f35 / 2);
                            } else {
                                float f44 = i19 + f36 + f37;
                                if (f15 < f44) {
                                    f21 = f44;
                                } else {
                                    Logcat.d("BubbleView", "normal");
                                    f21 = f15;
                                }
                                f42 = f21;
                                f18 = f11;
                            }
                        } else {
                            f18 = f17;
                        }
                        path3.lineTo(f42, f18);
                        path3.close();
                        Paint paint = bubbleView.f22153a;
                        canvas.drawPath(path3, paint);
                        float f45 = 2;
                        float f46 = bubbleView.f22164l / f45;
                        float f47 = f46 + f27;
                        RectF rectF = bubbleView.f22172t;
                        float f48 = bubbleView.f22162j;
                        int i20 = bubbleView.f22173u;
                        if (f47 > f12) {
                            Logcat.d("BubbleView", "bubble right");
                            f19 = (f12 - f48) - (i19 * 2);
                            rectF.set(f19, f30 - i20, f12, f30);
                        } else if (f27 - f46 > f36) {
                            Logcat.d("BubbleView", "bubble middle");
                            float f49 = f48 / f45;
                            f20 = f27 - f49;
                            rectF.set(f20 - (float) i19, f30 - i20, f49 + f27 + (float) i19, f30);
                            canvas.drawRoundRect(rectF, f33, bubbleView.f22171s, paint);
                            Paint paint2 = bubbleView.f22154b;
                            Paint.FontMetrics fontMetrics = paint2.getFontMetrics();
                            float centerY = (rectF.centerY() - (fontMetrics.top / f45)) - (fontMetrics.bottom / f45);
                            if (z10) {
                                canvas.scale(-1.0f, 1.0f, (f48 / f45) + f20, f35 / f45);
                            }
                            canvas.drawText(bubbleView.mText, f20, centerY, paint2);
                            i15 = i9;
                            i8 = 2;
                            i4 = 1;
                        } else {
                            Logcat.d("BubbleView", "bubble left");
                            f19 = f36 + f37;
                            rectF.set(f19, f30 - i20, f48 + f19 + (i19 * 2), f30);
                        }
                        f20 = i19 + f19;
                        canvas.drawRoundRect(rectF, f33, bubbleView.f22171s, paint);
                        Paint paint22 = bubbleView.f22154b;
                        Paint.FontMetrics fontMetrics2 = paint22.getFontMetrics();
                        float centerY2 = (rectF.centerY() - (fontMetrics2.top / f45)) - (fontMetrics2.bottom / f45);
                        if (z10) {
                        }
                        canvas.drawText(bubbleView.mText, f20, centerY2, paint22);
                        i15 = i9;
                        i8 = 2;
                        i4 = 1;
                    }
                }
                i9 = i16;
                i15 = i9;
                i8 = 2;
                i4 = 1;
            }
        }
    }
}
