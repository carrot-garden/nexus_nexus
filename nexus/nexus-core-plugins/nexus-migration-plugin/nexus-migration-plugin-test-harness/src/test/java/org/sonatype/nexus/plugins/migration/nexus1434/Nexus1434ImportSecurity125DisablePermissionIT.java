/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.migration.nexus1434;

import java.util.List;

import junit.framework.Assert;

import org.sonatype.nexus.plugin.migration.artifactory.dto.MigrationSummaryDTO;
import org.sonatype.nexus.rest.model.RepositoryTargetListResource;
import org.sonatype.security.rest.model.PlexusUserResource;
import org.sonatype.security.rest.model.PrivilegeStatusResource;
import org.sonatype.security.rest.model.RoleResource;

public class Nexus1434ImportSecurity125DisablePermissionIT
    extends AbstractImportSecurityIT
{

    public Nexus1434ImportSecurity125DisablePermissionIT()
    {
        super();
    }

    @Override
    protected void importSecurity()
        throws Exception
    {
        MigrationSummaryDTO migrationSummary = prepareMigration( getTestFile( "artifactory-security-125.zip" ) );

        // this is the most important part!
        migrationSummary.setResolvePermission( false );

        commitMigration( migrationSummary );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void verifySecurity()
        throws Exception
    {
        List<PlexusUserResource> userList = getImportedUserList();
        List<RepositoryTargetListResource> targetList = getImportedRepoTargetList();
        List<PrivilegeStatusResource> privilegeList = getImportedTargetPrivilegesList();
        List<RoleResource> roleList = getImportedRoleList();

        Assert.assertEquals( 4, userList.size() );
        Assert.assertTrue( targetList.isEmpty() );
        Assert.assertTrue( privilegeList.isEmpty() );
        Assert.assertTrue( roleList.isEmpty() );

        // these users are imported
        Assert.assertTrue( containUser( userList, "admin-artifactory" ) );
        Assert.assertTrue( containUser( userList, "admin1" ) );
        Assert.assertTrue( containUser( userList, "user" ) );
        Assert.assertTrue( containUser( userList, "user1" ) );

        // verify user-role mapping
        PlexusUserResource admin = getUserById( userList, "admin-artifactory" );
        Assert.assertEquals( 1, admin.getRoles().size() );
        containPlexusRole( admin.getRoles(), "admin" );

        PlexusUserResource admin1 = getUserById( userList, "admin1" );
        Assert.assertEquals( 1, admin1.getRoles().size() );
        containPlexusRole( admin1.getRoles(), "admin" );

        PlexusUserResource user = getUserById( userList, "user" );
        Assert.assertEquals( 1, user.getRoles().size() );
        containPlexusRole( user.getRoles(), "anonymous" );

        PlexusUserResource user1 = getUserById( userList, "user1" );
        Assert.assertEquals( 1, user1.getRoles().size() );
        containPlexusRole( user1.getRoles(), "anonymous" );
    }

}