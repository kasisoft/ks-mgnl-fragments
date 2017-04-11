package com.kasisoft.mgnl.fragments.internal;

import com.kasisoft.libs.common.i18n.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class Messages {
  
  @I18N("the following fragment definition is invalid: %s")
  public static I18NFormatter                 error_invalid_fragment_definition;

  @I18N("rendering the fragment '%s' failed. cause: %s")
  public static I18NFormatter                 error_failed_to_render;

  static {
    I18NSupport.initialize( Messages.class );
  }

} /* ENDCLASS */
