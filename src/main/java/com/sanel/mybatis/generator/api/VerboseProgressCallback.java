/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sanel.mybatis.generator.api;

import com.sanel.mybatis.generator.internal.NullProgressCallback;

/**
 * A slightly more verbose progress callback.
 * 
 * @author Jeff Butler
 * 
 */
public class VerboseProgressCallback extends NullProgressCallback {

    public VerboseProgressCallback() {
        super();
    }

    @Override
    public void startTask(String taskName) {
        System.out.println(taskName);
    }
}
