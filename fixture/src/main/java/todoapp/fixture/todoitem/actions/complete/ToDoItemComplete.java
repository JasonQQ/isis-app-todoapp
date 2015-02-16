/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package todoapp.fixture.todoitem.actions.complete;

import todoapp.dom.module.todoitem.ToDoItem;
import todoapp.dom.module.todoitem.ToDoItems;

import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class ToDoItemComplete extends FixtureScript {

    //region > toDoItem (input property)

    private ToDoItem toDoItem;

    /**
     * The item that was completed (output property
     */
    public ToDoItem getToDoItem() {
        return toDoItem;
    }

    private void setToDoItem(final ToDoItem toDoItem) {
        this.toDoItem = toDoItem;
    }
    //endregion

    @Override
    protected void execute(final ExecutionContext ec) {

        // find todoitem
        if(this.toDoItem == null) {
            throw new IllegalArgumentException("ToDoItem required");
        }

        // execute
        this.toDoItem.completed();

        // make results available
        ec.addResult(this, toDoItem);
    }

    //endregion

    @javax.inject.Inject
    private ToDoItems toDoItems;

    @javax.inject.Inject
    private ApplicationUsers applicationUsers;

}