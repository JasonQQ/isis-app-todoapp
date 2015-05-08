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
package todoapp.dom.app.similarto;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import todoapp.dom.module.categories.Category;
import todoapp.dom.module.todoitem.ToDoItem;
import todoapp.dom.module.todoitem.ToDoItemRepository;
import todoapp.dom.module.todoitem.ToDoItems;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class SimilarToContributions extends AbstractFactoryAndRepository {

    //region > similarTo (contributed collection)
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @Action(semantics = SemanticsOf.SAFE)
    public List<ToDoItem> similarTo(final ToDoItem toDoItem) {
        final List<ToDoItem> similarToDoItems = toDoItemRepository.findByAtPathAndCategory(currentUsersAtPath(), toDoItem.getCategory());
        return Lists.newArrayList(Iterables.filter(similarToDoItems, excluding(toDoItem)));
    }

    private static Predicate<ToDoItem> excluding(final ToDoItem toDoItem) {
        return new Predicate<ToDoItem>() {
            @Override
            public boolean apply(ToDoItem input) {
                return input != toDoItem;
            }
        };
    }
    //endregion

    //region > helpers
    protected String currentUsersAtPath() {
        final ApplicationUser me = meService.me();
        final ApplicationTenancy tenancy = me.getTenancy();
        if(tenancy == null) {
            throw new IllegalStateException("No application tenancy defined");
        }
        return tenancy.getPath();
    }
    //endregion

    //region > injected services

    @javax.inject.Inject
    private ToDoItems toDoItems;

    @javax.inject.Inject
    private ToDoItemRepository toDoItemRepository;

    @javax.inject.Inject
    private MeService meService;

    @javax.inject.Inject
    private QueryResultsCache queryResultsCache;
    //endregion

}
