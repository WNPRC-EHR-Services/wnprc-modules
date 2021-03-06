/*
 * Copyright (c) 2015 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.labkey.apikey;

import org.jetbrains.annotations.NotNull;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.ldk.ExtendedSimpleModule;
import org.labkey.api.module.DefaultModule;
import org.labkey.api.module.ModuleContext;
import org.labkey.api.view.WebPartFactory;
import org.labkey.apikey.api.ApiKeyService;
import org.labkey.apikey.api.JsonService;
import org.labkey.apikey.api.JsonServiceManager;
import org.labkey.apikey.service.ApiKeyServiceImpl;
import org.labkey.apikey.service.JsonServiceManagerImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ApiKeyModule extends ExtendedSimpleModule
{
    public static final String NAME = "ApiKey";

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public double getVersion()
    {
        return 15.11;
    }

    @Override
    public boolean hasScripts()
    {
        return true;
    }

    @Override
    @NotNull
    protected Collection<WebPartFactory> createWebPartFactories()
    {
        return Collections.emptyList();
    }

    @Override
    protected void init() {
        addController(ApiKeyController.NAME, ApiKeyController.class);

        JsonServiceManager.set(new JsonServiceManagerImpl());
        ApiKeyService.set(new ApiKeyServiceImpl());
    }

    @Override
    public void doStartupAfterSpringConfig(ModuleContext moduleContext)
    {
        // add a container listener so we'll know when our container is deleted:
        ContainerManager.addContainerListener(new ApiKeyContainerListener());
    }

    @Override
    @NotNull
    public Collection<String> getSummary(Container c)
    {
        return Collections.emptyList();
    }

    @Override
    @NotNull
    public Set<String> getSchemaNames()
    {
        return Collections.singleton(ApiKeySchema.NAME);
    }
}