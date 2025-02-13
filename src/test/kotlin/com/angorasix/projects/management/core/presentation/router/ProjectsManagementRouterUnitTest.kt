package com.angorasix.projects.management.core.presentation.router

import com.angorasix.projects.management.core.domain.management.ManagementStatus
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.BasePathConfigs
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.Route
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.RoutesConfigs
import com.angorasix.projects.management.core.presentation.dto.ProjectManagementDto
import com.angorasix.projects.management.core.presentation.handler.ProjectsManagementHandler
import com.angorasix.projects.management.core.utils.mockConstitutionDto
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpMethod
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.reactive.function.server.MockServerRequest.builder
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.reactive.function.server.EntityResponse
import java.net.URI

@ExtendWith(MockKExtension::class)
class ProjectsManagementRouterUnitTest {

    private lateinit var router: ProjectsManagementRouter

    @MockK
    private lateinit var apiConfigs: ApiConfigs

    @MockK
    private lateinit var handler: ProjectsManagementHandler

    private var routeConfigs: RoutesConfigs = RoutesConfigs(
        Route("mocked-create", listOf("mocked-base1"), HttpMethod.POST, "/"),
        Route(
            "mocked-create-by-projectId",
            listOf("mocked-base1"),
            HttpMethod.POST,
            "/project/{projectId}",
        ),
        Route("mocked-update", listOf("mocked-base1"), HttpMethod.PUT, "/{id}"),
        Route("mocked-validate-admin", listOf("mocked-base1"), HttpMethod.GET, "/isAdmin"),
        Route("mocked-get-single", listOf("mocked-base1"), HttpMethod.GET, "/{id}"),
        Route("mocked-list-project", listOf("mocked-base1"), HttpMethod.GET, "/"),
        Route(
            "mocked-get-single-by-projectId",
            listOf("mocked-base1"),
            HttpMethod.GET,
            "/project/{projectId}",
        ),
    )
    private var basePathsConfigs: BasePathConfigs = BasePathConfigs(
        "/projects-management",
        "/",
        "/{id}",
        "/project/{projectId}",
    )

    @BeforeEach
    fun init() {
        every { apiConfigs.routes } returns routeConfigs
        every { apiConfigs.basePaths } returns basePathsConfigs
        router = ProjectsManagementRouter(handler, apiConfigs)
    }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `Given Project router - When expected base APIs requested - Then router routes correctly`() =
        runTest {
            val outputRouter = router.managementRouterFunction()
            val mockedRequest = MockServerHttpRequest.get("/mocked")
            val mockedExchange = MockServerWebExchange.builder(mockedRequest)
                .build()
            val getAllProjectsRequest = builder().uri(URI("/projects-management/"))
                .exchange(mockedExchange)
                .build()
            val postProjectRequest = builder().method(HttpMethod.POST)
                .uri(URI("/projects-management/"))
                .exchange(mockedExchange)
                .body(
                    ProjectManagementDto(
                        "testProjectId",
                        emptySet(),
                        mockConstitutionDto(),
                        ManagementStatus.STARTUP,
                    ),
                )
            val invalidRequest = builder().uri(URI("/invalid-path"))
                .exchange(mockedExchange)
                .build()
            val mockedResponse = EntityResponse.fromObject("any").build().awaitSingle()
            coEvery { handler.listProjectManagements(getAllProjectsRequest) } returns mockedResponse
            coEvery { handler.createProjectManagement(postProjectRequest) } returns mockedResponse

            // if routes don't match, they will throw an exception as with the invalid Route no need to assert anything
            outputRouter.route(getAllProjectsRequest).awaitSingle()
                .handle(getAllProjectsRequest).awaitSingle()
            outputRouter.route(postProjectRequest).awaitSingle()
                .handle(postProjectRequest).awaitSingle()
            // disabled until junit-jupiter 5.7.0 is released and included to starter dependency
            assertThrows<NoSuchElementException> {
                outputRouter.route(invalidRequest)
                    .awaitSingle()
            }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `Given Project router - When expected by id APIs requested - Then router routes correctly`() =
        runTest {
            val outputRouter = router.managementRouterFunction()
            val mockedRequest = MockServerHttpRequest.get("/mocked")
            val mockedExchange = MockServerWebExchange.builder(mockedRequest)
                .build()
            val getSingleProjectRequest = builder().uri(URI("/projects-management/1"))
                .exchange(mockedExchange)
                .build()
            val isAdminRequest = builder().uri(URI("/projects-management/1/isAdmin"))
                .exchange(mockedExchange)
                .build()
            val putProjectRequest = builder().method(HttpMethod.PUT)
                .uri(URI("/projects-management/1"))
                .exchange(mockedExchange)
                .body(
                    ProjectManagementDto(
                        "testProjectId",
                        emptySet(),
                        mockConstitutionDto(),
                        ManagementStatus.STARTUP,
                    ),
                )
            val invalidRequest = builder().uri(URI("/invalid-path"))
                .exchange(mockedExchange)
                .build()
            val mockedResponse = EntityResponse.fromObject("any").build().awaitSingle()
            coEvery { handler.getProjectManagement(getSingleProjectRequest) } returns mockedResponse
            coEvery { handler.updateProjectManagement(putProjectRequest) } returns mockedResponse
            coEvery { handler.validateAdminUser(isAdminRequest) } returns mockedResponse

            // if routes don't match, they will throw an exception as with the invalid Route no need to assert anything
            outputRouter.route(isAdminRequest).awaitSingle()
                .handle(isAdminRequest).awaitSingle()
            outputRouter.route(getSingleProjectRequest).awaitSingle()
                .handle(getSingleProjectRequest).awaitSingle()
            outputRouter.route(putProjectRequest).awaitSingle()
                .handle(putProjectRequest).awaitSingle()
            // disabled until junit-jupiter 5.7.0 is released and included to starter dependency
            assertThrows<NoSuchElementException> {
                outputRouter.route(invalidRequest)
                    .awaitSingle()
            }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `Given Project router - When expected by project id APIs requested - Then router routes correctly`() =
        runTest {
            val outputRouter = router.managementRouterFunction()
            val mockedRequest = MockServerHttpRequest.get("/mocked")
            val mockedExchange = MockServerWebExchange.builder(mockedRequest)
                .build()
            val getByProjectIdRequest =
                builder().uri(URI("/projects-management/project/testProjectId1"))
                    .exchange(mockedExchange)
                    .build()
            val postByProjectIdRequest = builder().method(HttpMethod.POST)
                .uri(URI("/projects-management/project/testProjectId1"))
                .exchange(mockedExchange)
                .body(
                    ProjectManagementDto(
                        "testProjectId1",
                        emptySet(),
                        mockConstitutionDto(),
                        ManagementStatus.STARTUP,
                    ),
                )
            val invalidRequest = builder().uri(URI("/invalid-path"))
                .exchange(mockedExchange)
                .build()
            val mockedResponse = EntityResponse.fromObject("any").build().awaitSingle()
            coEvery { handler.createProjectManagementByProjectId(postByProjectIdRequest) } returns mockedResponse
            coEvery { handler.getProjectManagementByProjectId(getByProjectIdRequest) } returns mockedResponse

            // if routes don't match, they will throw an exception as with the invalid Route no need to assert anything
            outputRouter.route(postByProjectIdRequest).awaitSingle()
                .handle(postByProjectIdRequest).awaitSingle()
            outputRouter.route(getByProjectIdRequest).awaitSingle()
                .handle(getByProjectIdRequest).awaitSingle()
            // disabled until junit-jupiter 5.7.0 is released and included to starter dependency
            assertThrows<NoSuchElementException> {
                outputRouter.route(invalidRequest)
                    .awaitSingle()
            }
        }
}
