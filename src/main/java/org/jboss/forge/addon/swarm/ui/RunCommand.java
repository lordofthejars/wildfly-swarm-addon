/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.swarm.ui;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.building.BuildException;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.swarm.facet.WildFlySwarmFacet;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(WildFlySwarmFacet.class)
public class RunCommand extends AbstractWildFlySwarmCommand
{
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("WildFly Swarm: Run")
               .description("Run the project using the 'wildfly-swarm:run' maven plugin")
               .category(Categories.create("WildFly Swarm"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Project project = getSelectedProject(context);
      UIOutput output = context.getUIContext().getProvider().getOutput();
      PackagingFacet packagingFacet = project.getFacet(PackagingFacet.class);
      try
      {
         packagingFacet.createBuilder().addArguments("wildfly-swarm:run").runTests(false).build(output.out(),
                  output.err());
      }
      catch (BuildException ie)
      {
         if (!(ie.getCause() instanceof InterruptedException))
         {
            return Results.fail("Error while running the build", ie.getCause());
         }
      }
      return Results.success();
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      UIProvider provider = context.getProvider();
      return super.isEnabled(context) && !provider.isGUI() && !provider.isEmbedded();
   }
}
