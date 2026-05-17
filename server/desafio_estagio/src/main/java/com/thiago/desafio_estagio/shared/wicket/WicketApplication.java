package com.thiago.desafio_estagio.shared.wicket;

import com.thiago.desafio_estagio.shared.utils.JsUtils;
import com.thiago.desafio_estagio.shared.wicket.pages.detalhes.DetalhesClientePage;
import com.thiago.desafio_estagio.shared.wicket.pages.home.HomePage;
import com.thiago.desafio_estagio.shared.wicket.styles.CssResources;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class WicketApplication extends WebApplication {

    public static final PackageResourceReference THEME_CSS   = new PackageResourceReference(CssResources.class, "theme.css");
    public static final PackageResourceReference TOKENS_CSS  = new PackageResourceReference(CssResources.class, "tokens.css");
    public static final PackageResourceReference GLOBAL_CSS  = new PackageResourceReference(CssResources.class, "global.css");
    public static final PackageResourceReference LAYOUT_CSS  = new PackageResourceReference(CssResources.class, "layout.css");
    public static final PackageResourceReference TOOLBAR_CSS = new PackageResourceReference(CssResources.class, "toolbar.css");
    public static final PackageResourceReference LIST_CSS    = new PackageResourceReference(CssResources.class, "list.css");
    public static final PackageResourceReference MODAL_CSS   = new PackageResourceReference(CssResources.class, "modal.css");
    public static final PackageResourceReference DETAIL_CSS  = new PackageResourceReference(CssResources.class, "detail.css");

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
        // Em modo development o Wicket mantém as tags <wicket:container>, <wicket:panel> etc no HTML
        // renderizado, que aparecem como elementos inline desconhecidos no browser e quebram layouts
        // baseados em CSS Grid/Flex que esperam os filhos diretos no host (ex: erp-list-row).
        getMarkupSettings().setStripWicketTags(true);
        // Serve os recursos diretamente pelo Wicket evitando conflito com o WicketFilter
        mountResource("/css/theme.css",   THEME_CSS);
        mountResource("/css/tokens.css",  TOKENS_CSS);
        mountResource("/css/global.css",  GLOBAL_CSS);
        mountResource("/css/layout.css",  LAYOUT_CSS);
        mountResource("/css/toolbar.css", TOOLBAR_CSS);
        mountResource("/css/list.css",    LIST_CSS);
        mountResource("/css/modal.css",   MODAL_CSS);
        mountResource("/css/detail.css",  DETAIL_CSS);
        mountResource("/js/masks.js",     JsUtils.MASKS);
        mountPage("/clientes/detalhe/${id}", DetalhesClientePage.class);
    }
}
