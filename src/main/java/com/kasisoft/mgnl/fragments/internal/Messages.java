package com.kasisoft.mgnl.fragments.internal;

import com.kasisoft.libs.common.i18n.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class Messages {
  
  @I18N("Installing filter servlet")
  public static String                        task_install_filter_servlet;
  
  @I18N("Installing filter servlet '%s'")
  public static I18NFormatter                 task_install_filter_servlet_desc;
  
  @I18N("Positioning filter servlet '%s' after '%s'")
  public static I18NFormatter                 task_positioning_filter_servlet;
  
  static {
    I18NSupport.initialize( Messages.class );
  }

} /* ENDCLASS */
