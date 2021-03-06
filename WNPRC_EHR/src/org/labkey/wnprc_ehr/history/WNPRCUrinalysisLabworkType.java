/*
 * Copyright (c) 2013-2015 LabKey Corporation
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
package org.labkey.wnprc_ehr.history;

import org.labkey.api.ehr.history.SortingLabworkType;
import org.labkey.api.module.Module;

/**
 * User: bimber
 * Date: 3/6/13
 * Time: 12:27 PM
 */
public class WNPRCUrinalysisLabworkType extends SortingLabworkType
{
    public WNPRCUrinalysisLabworkType(Module module)
    {
        super("Urinalysis", "study", "Urinalysis Results", "Urinalysis", module);
    }
}
