package com.sahaab.quantumpaperui.widget;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

public class GATextView
  extends TextView
{
  public GATextView(Context paramContext)
  {
    super(paramContext);
  }
  
  public GATextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public GATextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public GATextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1);
  }
 
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    Layout localLayout = getLayout();
    if (localLayout != null)
    {
      int i = localLayout.getLineCount();
      if ((i > 0) && (localLayout.getEllipsisCount(i - 1) > 0))
      {
        setTextSize(0, getTextSize() - 1.0F);
        super.onMeasure(paramInt1, paramInt2);
      }
    }
  }
}
