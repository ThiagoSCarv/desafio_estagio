package com.thiago.desafio_estagio.shared.wicket;

import com.thiago.desafio_estagio.shared.utils.JsUtils;
import com.thiago.desafio_estagio.shared.wicket.pages.home.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class WicketApplication extends WebApplication {

    public static final PackageResourceReference THEME_CSS =
        new PackageResourceReference(WicketApplication.class, "theme.css");

    private final ApplicationContext applicationContext;

    public WicketApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

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
        // Serve os recursos diretamente pelo Wicket evitando conflito com o WicketFilter
        mountResource("/css/theme.css", THEME_CSS);
        mountResource("/js/masks.js", JsUtils.MASKS);
    }
}
