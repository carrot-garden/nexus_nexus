/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.feeds.record;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonatype.nexus.ApplicationStatusSource;
import org.sonatype.nexus.feeds.FeedRecorder;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.RequestContext;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.events.RepositoryItemEventStoreCreate;
import org.sonatype.nexus.proxy.item.AbstractRepositoryItemUidFactory;
import org.sonatype.nexus.proxy.item.DefaultRepositoryItemUid;
import org.sonatype.nexus.proxy.item.DefaultStorageFileItem;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.item.uid.IsHiddenAttribute;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

public class ItemChangesFeedEventInspectorTest
    extends TestSupport
{

    @Mock
    private FeedRecorder feedRecorder;

    @Mock
    private ApplicationStatusSource applicationStatusSource;

    @Mock
    private Repository repository;

    @Mock
    private StorageFileItem storageFileItem;
    
    @Mock
    private RepositoryItemUid repositoryItemUid;

    @Before
    public void setup()
    {
        when( repository.getId() ).thenReturn( "test" );
        when( storageFileItem.getItemContext() ).thenReturn( new RequestContext(  ) );
        when( storageFileItem.getRepositoryItemUid() ).thenReturn( repositoryItemUid );
    }

    @Test
    public void eventsOnHiddenFilesAreNotRecorded()
    {
        final ItemChangesFeedEventInspector underTest = new ItemChangesFeedEventInspector(
            feedRecorder, applicationStatusSource
        );

        final RepositoryItemEventStoreCreate evt = new RepositoryItemEventStoreCreate( repository, storageFileItem );
        
        when( repositoryItemUid.getBooleanAttributeValue( IsHiddenAttribute.class ) ).thenReturn( true );
        
        underTest.inspect( evt );

        verifyNoMoreInteractions( feedRecorder );
    }

}