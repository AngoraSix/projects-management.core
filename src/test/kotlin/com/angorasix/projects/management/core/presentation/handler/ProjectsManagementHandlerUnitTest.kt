package com.angorasix.projects.management.core.presentation.handler

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.projects.management.core.application.ProjectsManagementService
import com.angorasix.projects.management.core.domain.management.ManagementStatus
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.HeadersConfigs
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.Route
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.RoutesConfigs
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import com.angorasix.projects.management.core.presentation.dto.ProjectManagementDto
import com.angorasix.projects.management.core.utils.mockConstitutionDto
import com.angorasix.projects.management.core.utils.mockProjectManagement
import com.angorasix.projects.management.core.utils.mockProjectManagementDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.mediatype.problem.Problem.ExtendedProblem
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.reactive.function.server.EntityResponse
import org.springframework.web.reactive.function.server.ServerRequest

@ExtendWith(MockKExtension::class)
@ExperimentalCoroutinesApi
class ProjectsManagementHandlerUnitTest {

    private lateinit var handler: ProjectsManagementHandler

    @MockK
    private lateinit var service: ProjectsManagementService

    @MockK
    private lateinit var apiConfigs: ApiConfigs

    private var headerConfigs: HeadersConfigs = HeadersConfigs("MockedContributorHeader")

    private var routeConfigs: RoutesConfigs = RoutesConfigs(
        "",
        "/{id}",
        "/project/{projectId}",
        Route("mocked-create", listOf("mocked-base1"), HttpMethod.POST, ""),
        Route("mocked-create-by-projectId", listOf("mocked-base1"), HttpMethod.POST, ""),
        Route("mocked-update", listOf("mocked-base1"), HttpMethod.PUT, "/{id}"),
        Route("mocked-get-single", listOf("mocked-base1"), HttpMethod.GET, "/{id}"),
        Route("mocked-list-project", listOf("mocked-base1"), HttpMethod.GET, ""),
        Route("mocked-get-single-by-projectId", listOf("mocked-base1"), HttpMethod.GET, ""),
    )

    @BeforeEach
    fun init() {
        every { apiConfigs.headers } returns headerConfigs
        every { apiConfigs.routes } returns routeConfigs
        handler = ProjectsManagementHandler(service, apiConfigs)
    }

    @Test
    @Throws(Exception::class)
    fun `Given existing project managements - When list managements - Then handler retrieves Ok Response`() =
        runTest {
            val mockedExchange = MockServerWebExchange.from(
                MockServerHttpRequest.get(routeConfigs.listProjectManagements.path).build(),
            )
            val mockedRequest: ServerRequest =
                MockServerRequest.builder().exchange(mockedExchange).build()
            val mockedProjectManagement =
                mockProjectManagement()
            val retrievedProjectManagement = flowOf(mockedProjectManagement)
            coEvery { service.findProjectManagements(ListProjectsManagementFilter()) } returns retrievedProjectManagement

            val outputResponse = handler.listProjectManagements(mockedRequest)

            assertThat(outputResponse.statusCode()).isEqualTo(HttpStatus.OK)
            val response = @Suppress("UNCHECKED_CAST")
            outputResponse as EntityResponse<Flow<ProjectManagementDto>>
            val responseBody = response.entity()
            responseBody.collect {
                assertThat(it.constitution).isNotNull
                assertThat(it.projectId).isEqualTo("mockedProjectId")
                assertThat(it.status).isEqualTo(ManagementStatus.STARTUP)
            }
            coVerify { service.findProjectManagements(ListProjectsManagementFilter()) }
        }

    @Test
    @Throws(Exception::class)
    fun `Given request with project and RequestingContributor - When create project - Then handler retrieves Created`() =
        runBlocking { // = runBlockingTest { // until we resolve why service.createProject is hanging https://github.com/Kotlin/kotlinx.coroutines/issues/1204
            val mockedProjectManagementDto = mockProjectManagementDto()
            val mockedSimpleContributor = SimpleContributor("mockedId")
            val mockedExchange = MockServerWebExchange.from(
                MockServerHttpRequest.get(routeConfigs.createProjectManagement.path).build(),
            )
            val mockedRequest: ServerRequest = MockServerRequest.builder()
                .attribute(headerConfigs.contributor, mockedSimpleContributor)
                .exchange(mockedExchange).body(mono { mockedProjectManagementDto })
            val mockedProjectManagement = mockProjectManagement()
            coEvery { service.createProjectManagement(ofType(ProjectManagement::class)) } returns mockedProjectManagement

            val outputResponse = handler.createProjectManagement(mockedRequest)

            assertThat(outputResponse.statusCode()).isEqualTo(HttpStatus.CREATED)
            val response = @Suppress("UNCHECKED_CAST")
            outputResponse as EntityResponse<ProjectManagementDto>
            val responseBody = response.entity()
            assertThat(responseBody).isNotSameAs(mockedProjectManagementDto)
            assertThat(responseBody.projectId).isEqualTo("mockedProjectId")
            assertThat(responseBody.constitution).isNotNull
            assertThat(responseBody.status).isEqualTo(ManagementStatus.STARTUP)
            coVerify { service.createProjectManagement(ofType(ProjectManagement::class)) }
        }

    @Test
    @Throws(Exception::class)
    fun `Given request with project and no RequestingContributor - When create project - Then handler retrieves Bad Request`() =
        runBlocking { // = runBlockingTest { // until we resolve why service.createProject is hanging https://github.com/Kotlin/kotlinx.coroutines/issues/1204
            val mockedProjectManagementDto = mockProjectManagementDto()
            val mockedExchange = MockServerWebExchange.from(
                MockServerHttpRequest.get(routeConfigs.createProjectManagement.path).build(),
            )
            val mockedRequest: ServerRequest =
                MockServerRequest.builder().exchange(mockedExchange)
                    .body(mono { mockedProjectManagementDto })
            val mockedProject = mockProjectManagement()
            coEvery { service.createProjectManagement(ofType(ProjectManagement::class)) } returns mockedProject

            val outputResponse = handler.createProjectManagement(mockedRequest)

            assertThat(outputResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            val response = @Suppress("UNCHECKED_CAST")
            outputResponse as EntityResponse<ExtendedProblem<Any>>
            val responseBody = response.entity()
            assertThat(responseBody.status).isEqualTo(400)
            var properties = responseBody.properties as Map<String, Any>?
            assertThat(properties?.get("errorCode") as String).isEqualTo("CONTRIBUTOR_HEADER_INVALID")
            Unit
        }

    @Test
    @Throws(Exception::class)
    fun `Given request with invalid project management - When update project management - Then handler retrieves Bad Request`() =
        runBlocking { // = runBlockingTest { // until we resolve why service.createProject is hanging https://github.com/Kotlin/kotlinx.coroutines/issues/1204
            val mockedProjectManagementDto =
                ProjectManagementDto(null, emptySet(), mockConstitutionDto(), ManagementStatus.STARTUP)
            val mockedSimpleContributor = SimpleContributor("mockedId")
            val mockedExchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/id1-mocked").build(),
            )
            val mockedRequest: ServerRequest =
                MockServerRequest.builder().exchange(mockedExchange)
                    .attribute(headerConfigs.contributor, mockedSimpleContributor)
                    .pathVariable("id", "id1")
                    .body(mono { mockedProjectManagementDto })
            val outputResponse = handler.createProjectManagement(mockedRequest)

            assertThat(outputResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            val response = @Suppress("UNCHECKED_CAST")
            outputResponse as EntityResponse<EntityModel<ExtendedProblem<Any>>>
            val responseBody = response.entity()
            assertThat(responseBody.content?.status).isEqualTo(400)
            var properties = responseBody.content?.properties as Map<String, Any>?
            assertThat(properties?.get("errorCode") as String).isEqualTo("PROJECT_MANAGEMENT_INVALID")
            Unit
        }

    @Test
    @Throws(Exception::class)
    fun `Given request with project and RequestingContributor - When update project - Then handler retrieves Updated`() =
        runBlocking { // = runBlockingTest { // until we resolve why service.createProject is hanging https://github.com/Kotlin/kotlinx.coroutines/issues/1204
            val mockedProjectManagementDto = mockProjectManagementDto()
            val mockedSimpleContributor = SimpleContributor("mockedId")
            val mockedExchange =
                MockServerWebExchange.from(MockServerHttpRequest.get("/id1-mocked").build())
            val mockedRequest: ServerRequest = MockServerRequest.builder()
                .attribute(headerConfigs.contributor, mockedSimpleContributor)
                .pathVariable("id", "id1").exchange(mockedExchange)
                .body(mono { mockedProjectManagementDto })
            val mockedProjectManagement = mockProjectManagement("Updated")
            coEvery {
                service.updateProjectManagement(
                    "id1",
                    ofType(ProjectManagement::class),
                )
            } returns mockedProjectManagement

            val outputResponse = handler.updateProjectManagement(mockedRequest)

            assertThat(outputResponse.statusCode()).isEqualTo(HttpStatus.OK)
            val response = @Suppress("UNCHECKED_CAST")
            outputResponse as EntityResponse<ProjectManagementDto>
            val responseBody = response.entity()
            assertThat(responseBody).isNotSameAs(mockedProjectManagementDto)
            assertThat(responseBody.constitution).isNotNull
            assertThat(responseBody.projectId).isEqualTo("mockedProjectIdUpdated")
            coVerify {
                service.updateProjectManagement(
                    "id1",
                    ofType(ProjectManagement::class),
                )
            }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `Given existing projects - When get project for non Admin contributor - Then handler retrieves Ok Response without Edit link`() =
        runTest {
            val projectId = "projectId"
            val mockedSimpleContributor = SimpleContributor("mockedId")
            val mockedExchange =
                MockServerWebExchange.from(MockServerHttpRequest.get("/id1-mocked").build())
            val mockedRequest: ServerRequest =
                MockServerRequest.builder()
                    .attribute(headerConfigs.contributor, mockedSimpleContributor)
                    .pathVariable("id", projectId).exchange(mockedExchange).build()
            val mockedProjectManagement =
                mockProjectManagement()
            coEvery { service.findSingleProjectManagement(projectId) } returns mockedProjectManagement

            val outputResponse = handler.getProjectManagement(mockedRequest)

            assertThat(outputResponse.statusCode()).isEqualTo(HttpStatus.OK)
            val responseBody =
                @Suppress("UNCHECKED_CAST")
                (outputResponse as EntityResponse<ProjectManagementDto>).entity()
            assertThat(responseBody.constitution).isNotNull
            assertThat(responseBody.links.count()).isGreaterThan(1)
            assertThat(responseBody.links.getLink("updateProject")).isEmpty
            coVerify { service.findSingleProjectManagement(projectId) }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `Given existing projects - When get project for Admin Contributor - Then handler retrieves Ok Response with Edit link`() =
        runTest {
            val projectId = "projectId"
            val mockedSimpleContributor = SimpleContributor("mockedId", emptySet())

            val mockedExchange =
                MockServerWebExchange.from(MockServerHttpRequest.get("/id1-mocked").build())
            val mockedRequest: ServerRequest =
                MockServerRequest.builder()
                    .attribute(headerConfigs.contributor, mockedSimpleContributor)
                    .pathVariable("id", projectId).exchange(mockedExchange).build()
            val mockedProjectManagement =
                mockProjectManagement(admins= setOf(mockedSimpleContributor))
            coEvery { service.findSingleProjectManagement(projectId) } returns mockedProjectManagement

            val outputResponse = handler.getProjectManagement(mockedRequest)

            assertThat(outputResponse.statusCode()).isEqualTo(HttpStatus.OK)
            val responseBody =
                @Suppress("UNCHECKED_CAST")
                (outputResponse as EntityResponse<ProjectManagementDto>).entity()
            assertThat(responseBody.constitution).isNotNull
            assertThat(responseBody.links.count()).isGreaterThan(2)
            assertThat(responseBody.links.getLink("updateProject")).isNotNull
            coVerify { service.findSingleProjectManagement(projectId) }
        }
}
