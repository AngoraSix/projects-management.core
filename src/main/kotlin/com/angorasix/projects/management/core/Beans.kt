package com.angorasix.projects.management.core

import com.angorasix.projects.core.infrastructure.security.ProjectsManagementCoreSecurityConfiguration
import com.angorasix.projects.management.core.application.ProjectsManagementService
import com.angorasix.projects.management.core.presentation.handler.ProjectsManagementHandler
import com.angorasix.projects.management.core.presentation.router.ProjectsManagementRouter
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

val beans = beans {
    bean {
        ProjectsManagementCoreSecurityConfiguration().springSecurityFilterChain(ref())
    }
    bean<ProjectsManagementService>()
    bean<ProjectsManagementHandler>()
    bean {
        ProjectsManagementRouter(ref(), ref(), ref()).projectRouterFunction()
    }
}

class BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) = beans.initialize(context)
}
