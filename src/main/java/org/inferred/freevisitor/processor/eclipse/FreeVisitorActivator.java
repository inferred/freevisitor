package org.inferred.freevisitor.processor.eclipse;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class FreeVisitorActivator extends Plugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "org.inferred.freevisitor";

  // The shared instance
  private static FreeVisitorActivator plugin;

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static FreeVisitorActivator getDefault() {
    return plugin;
  }
}