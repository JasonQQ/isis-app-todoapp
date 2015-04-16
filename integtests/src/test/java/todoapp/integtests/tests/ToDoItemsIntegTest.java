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
package todoapp.integtests.tests;

import java.util.List;

import javax.inject.Inject;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import todoapp.dom.module.categories.Category;
import todoapp.dom.module.categories.Subcategory;
import todoapp.dom.module.todoitem.ToDoItem;
import todoapp.dom.module.todoitem.ToDoItems;
import todoapp.integtests.fixture.ToDoItemsIntegTestFixture;

public class ToDoItemsIntegTest extends AbstractToDoIntegTest {

    @Inject
    DomainObjectContainer container;
    @Inject
    FixtureScripts fixtureScripts;
    @Inject
    ToDoItems toDoItems;

    public static class Finders extends ToDoItemsIntegTest {

        ToDoItemsIntegTestFixture fixture;

        @Before
        public void setUpData() throws Exception {
            // executing the fixtures directly allows us to look up the results later.
            fixtureScripts.runFixtureScript(fixture = new ToDoItemsIntegTestFixture(), null);
        }

        private int notYetCompletedSize;
        private int completedSize;

        @Before
        public void setUp() throws Exception {

            // could use fixture#lookup(...), but can also just search directly.
            final List<ToDoItem> notYetCompleteItems = wrap(toDoItems).notYetComplete();
            final List<ToDoItem> completedItems = wrap(toDoItems).complete();

            notYetCompletedSize = notYetCompleteItems.size();
            completedSize = completedItems.size();

            assertThat(notYetCompletedSize, is(Matchers.greaterThan(5)));
        }

        @Test
        public void complete_and_notYetComplete() throws Exception {

            // given
            List<ToDoItem> notYetCompleteItems = wrap(toDoItems).notYetComplete();
            final ToDoItem toDoItem = wrap(notYetCompleteItems.get(0));
            nextTransaction();

            // when
            toDoItem.completed();
            nextTransaction();

            // then
            assertThat(wrap(toDoItems).notYetComplete().size(), is(notYetCompletedSize-1));
            assertThat(wrap(toDoItems).complete().size(), is(completedSize+1));
            nextTransaction();

            // and when
            toDoItem.notYetCompleted();
            nextTransaction();

            // then
            assertThat(wrap(toDoItems).notYetComplete().size(), is(notYetCompletedSize));
            assertThat(wrap(toDoItems).complete().size(), is(completedSize));
        }
    }

    public static class NewToDo_and_Delete extends ToDoItemsIntegTest {

        @Before
        public void setUpData() throws Exception {
            // none
        }

        @Test
        public void happyCase() throws Exception {

            // given
            int size = wrap(toDoItems).notYetComplete().size();
            nextTransaction();

            // when
            final ToDoItem newToDo = toDoItems.newToDo("new todo", Category.PROFESSIONAL, Subcategory.OPEN_SOURCE, null, null);
            nextTransaction();

            // then
            assertThat(newToDo.getDescription(), is("new todo"));
            assertThat(newToDo.getCategory(), is(Category.PROFESSIONAL));
            assertThat(wrap(toDoItems).notYetComplete().size(), is(size+1));
            assertThat(container.isPersistent(newToDo), is(true));
            assertThat(container().isPersistent(wrap(newToDo)), is(true));

            nextTransaction();

            // when
            wrap(newToDo).delete();
            nextTransaction();

            // then
            assertThat(wrap(toDoItems).notYetComplete().size(), is(size));
        }

    }

}