package com.angorasix.projects.management.core.application

import com.angorasix.projects.management.core.domain.management.ManagementStatus
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.domain.management.ProjectManagementRepository
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import com.angorasix.projects.management.core.utils.mockConstitution
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifyAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProjectsManagementServiceUnitTest {
    private lateinit var service: ProjectsManagementService

    @MockK
    private lateinit var repository: ProjectManagementRepository

    @BeforeEach
    fun init() {
        service = ProjectsManagementService(repository)
    }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `given existing projects - when request find projects - then receive projects`() =
        runTest {
            val mockedProjectManagement = ProjectManagement(
                "mockedProjectId",
                mockConstitution(),
                ManagementStatus.STARTUP,
            )
            val filter = ListProjectsManagementFilter()
            coEvery { repository.findUsingFilter(filter) } returns flowOf(mockedProjectManagement)

            val outputProjectManagements = service.findProjectManagements(filter)

            outputProjectManagements.collect {
                assertThat(it).isSameAs(mockedProjectManagement)
            }
            coVerify { repository.findUsingFilter(filter) }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `given existing project management - when find single project managements - then service retrieves mono with project management`() =
        runTest {
            val mockedProjectManagementId = "id1"
            val mockedProjectManagement = ProjectManagement(
                "mockedProjectId",
                mockConstitution(),
                ManagementStatus.STARTUP,
            )
            coEvery { repository.findById(mockedProjectManagementId) } returns mockedProjectManagement
            val outputProjectManagement =
                service.findSingleProjectManagement(mockedProjectManagementId)
            assertThat(outputProjectManagement).isSameAs(mockedProjectManagement)
            coVerify { repository.findById(mockedProjectManagementId) }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `when create project management - then service retrieve saved project management`() =
        runTest {
            val mockedProjectManagement = ProjectManagement(
                "mockedProjectId",
                mockConstitution(),
                ManagementStatus.STARTUP,
            )
            val savedProjectManagement = ProjectManagement(
                "savedProjectId",
                mockConstitution(),
                ManagementStatus.STARTUP,
            )
            coEvery { repository.save(mockedProjectManagement) } returns savedProjectManagement
            val outputProjectManagement =
                service.createProjectManagement(mockedProjectManagement)
            assertThat(outputProjectManagement).isSameAs(savedProjectManagement)
            coVerify { repository.save(mockedProjectManagement) }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `when update project management - then service retrieve saved project management`() =
        runTest {
            val mockedExistingProjectManagement = mockk<ProjectManagement>()
            every {
                mockedExistingProjectManagement.setProperty(ProjectManagement::status.name) value ManagementStatus.OPERATIONAL
            } just Runs
            every {
                mockedExistingProjectManagement.projectId
            } returns "mockedProjectId"
            val mockedUpdateProjectManagement = ProjectManagement(
                "mockedProjectId",
                mockConstitution(),
                ManagementStatus.OPERATIONAL,
            )
            val savedProjectManagement = ProjectManagement(
                "savedMockedProjectId",
                mockConstitution(),
                ManagementStatus.STARTUP,
            )
            coEvery { repository.findById("id1") } returns mockedExistingProjectManagement
            coEvery { repository.save(any()) } returns savedProjectManagement
            val outputProjectManagement =
                service.updateProjectManagement("id1", mockedUpdateProjectManagement)
            assertThat(outputProjectManagement).isSameAs(savedProjectManagement)
            coVerifyAll {
                repository.findById("id1")
                repository.save(any())
            }
            verifyAll {
                mockedExistingProjectManagement.setProperty(ProjectManagement::status.name) value ManagementStatus.OPERATIONAL
                mockedExistingProjectManagement.projectId
            }
            confirmVerified(mockedExistingProjectManagement, repository)
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun whenUpdateProjectManagement_thenServiceRetrieveUpdatedProjectManagement() =
        runTest {
            val mockedProjectManagement = ProjectManagement(
                "mockedId",
                mockConstitution(),
                ManagementStatus.STARTUP,
            )
            val updatedProjectManagement = ProjectManagement(
                "mockedId",
                mockConstitution(),
                ManagementStatus.OPERATIONAL,
            )
            coEvery { repository.save(mockedProjectManagement) } returns updatedProjectManagement
            val outputProjectManagement =
                service.createProjectManagement(mockedProjectManagement)
            assertThat(outputProjectManagement).isSameAs(updatedProjectManagement)
            coVerify { repository.save(mockedProjectManagement) }
        }
}
