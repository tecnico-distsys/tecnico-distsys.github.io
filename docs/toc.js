// Populate the sidebar
//
// This is a script, and not included directly in the page, to control the total size of the book.
// The TOC contains an entry for each page, so if each page includes a copy of the TOC,
// the total size of the page becomes O(n**2).
class MDBookSidebarScrollbox extends HTMLElement {
    constructor() {
        super();
    }
    connectedCallback() {
        this.innerHTML = '<ol class="chapter"><li class="chapter-item expanded affix "><a href="index.html">Laboratórios de Sistemas Distribuídos (edição 2025)</a></li><li class="chapter-item expanded affix "><li class="spacer"></li><li class="chapter-item expanded affix "><div>Antes de começar</div></li><li class="chapter-item expanded affix "><a href="00-software.html">Instalação das ferramentas de desenvolvimento</a></li><li class="chapter-item expanded affix "><a href="00-software-faq.html">Perguntas frequentes sobre as ferramentas</a></li><li class="chapter-item expanded affix "><li class="spacer"></li><li class="chapter-item expanded affix "><div>Guiões laboratoriais durante o período</div></li><li class="chapter-item expanded "><a href="01-ferramentas.html"><strong aria-hidden="true">1.</strong> Apresentação e ferramentas (Java, Maven, IDE)</a></li><li class="chapter-item expanded "><a href="02-java-avancado.html"><strong aria-hidden="true">2.</strong> Java avançado (exceções, concorrência e troca de mensagens)</a></li><li class="chapter-item expanded "><a href="03-grpc.html"><strong aria-hidden="true">3.</strong> gRPC: conceitos base</a></li><li class="chapter-item expanded "><a href="04-grpc-erros.html"><strong aria-hidden="true">4.</strong> gRPC: tratamento de erros e métodos remotos bloqueantes</a></li><li class="chapter-item expanded "><a href="05-grpc-multilinguagem.html"><strong aria-hidden="true">5.</strong> gRPC: multi-linguagem</a></li><li class="chapter-item expanded "><a href="06-grpc-assincronas.html"><strong aria-hidden="true">6.</strong> gRPC: chamadas assíncronas</a></li><li class="chapter-item expanded "><a href="07-grpc-metadata.html"><strong aria-hidden="true">7.</strong> gRPC: metadados</a></li><li class="chapter-item expanded affix "><li class="spacer"></li></ol>';
        // Set the current, active page, and reveal it if it's hidden
        let current_page = document.location.href.toString();
        if (current_page.endsWith("/")) {
            current_page += "index.html";
        }
        var links = Array.prototype.slice.call(this.querySelectorAll("a"));
        var l = links.length;
        for (var i = 0; i < l; ++i) {
            var link = links[i];
            var href = link.getAttribute("href");
            if (href && !href.startsWith("#") && !/^(?:[a-z+]+:)?\/\//.test(href)) {
                link.href = path_to_root + href;
            }
            // The "index" page is supposed to alias the first chapter in the book.
            if (link.href === current_page || (i === 0 && path_to_root === "" && current_page.endsWith("/index.html"))) {
                link.classList.add("active");
                var parent = link.parentElement;
                if (parent && parent.classList.contains("chapter-item")) {
                    parent.classList.add("expanded");
                }
                while (parent) {
                    if (parent.tagName === "LI" && parent.previousElementSibling) {
                        if (parent.previousElementSibling.classList.contains("chapter-item")) {
                            parent.previousElementSibling.classList.add("expanded");
                        }
                    }
                    parent = parent.parentElement;
                }
            }
        }
        // Track and set sidebar scroll position
        this.addEventListener('click', function(e) {
            if (e.target.tagName === 'A') {
                sessionStorage.setItem('sidebar-scroll', this.scrollTop);
            }
        }, { passive: true });
        var sidebarScrollTop = sessionStorage.getItem('sidebar-scroll');
        sessionStorage.removeItem('sidebar-scroll');
        if (sidebarScrollTop) {
            // preserve sidebar scroll position when navigating via links within sidebar
            this.scrollTop = sidebarScrollTop;
        } else {
            // scroll sidebar to current active section when navigating via "next/previous chapter" buttons
            var activeSection = document.querySelector('#sidebar .active');
            if (activeSection) {
                activeSection.scrollIntoView({ block: 'center' });
            }
        }
        // Toggle buttons
        var sidebarAnchorToggles = document.querySelectorAll('#sidebar a.toggle');
        function toggleSection(ev) {
            ev.currentTarget.parentElement.classList.toggle('expanded');
        }
        Array.from(sidebarAnchorToggles).forEach(function (el) {
            el.addEventListener('click', toggleSection);
        });
    }
}
window.customElements.define("mdbook-sidebar-scrollbox", MDBookSidebarScrollbox);
