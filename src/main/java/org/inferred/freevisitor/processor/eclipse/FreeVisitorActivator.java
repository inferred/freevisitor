package org.inferred.freevisitor.processor.eclipse;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class FreeVisitorActivator extends Plugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "sk.seges.corpis.core.pap.transfer";

  // The shared instance
  private static FreeVisitorActivator plugin;

  /**
   * The constructor
   */
  public FreeVisitorActivator() {
  }

  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static FreeVisitorActivator getDefault() {
    return plugin;
  }
}