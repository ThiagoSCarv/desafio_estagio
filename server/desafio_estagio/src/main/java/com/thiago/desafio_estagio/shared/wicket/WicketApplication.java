package com.thiago.desafio_estagio.shared.wicket;

import com.thiago.desafio_estagio.shared.utils.JsUtils;
import com.thiago.desafio_estagio.shared.wicket.pages.home.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class WicketApplication extends WebApplication {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected void init() {
        super.init();
        getComponentInstantiationListeners().add(
            new SpringComponentInjector(this, applicationContext)
        );
        // Serve o tema CSS diretamente pelo Wicket evitando conflito com o WicketFilter
        mountResource("/css/theme.css",
            new org.apache.wicket.request.resource.PackageResourceReference(
                WicketApplication.class, "theme.css"
            )
        );
        mountResource("/js/masks.js", JsUtils.MASKS);
    }
}
