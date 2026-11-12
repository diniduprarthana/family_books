(function () {
  const navToggle = document.getElementById("navToggle");
  const navPanel = document.getElementById("navPanel");
  if (navToggle && navPanel) {
    navToggle.addEventListener("click", function () {
      navPanel.classList.toggle("open");
      const expanded = navPanel.classList.contains("open");
      navToggle.setAttribute("aria-expanded", expanded ? "true" : "false");
    });
  }

  const siteNav = document.querySelector(".site-nav");
  if (siteNav && document.body.classList.contains("page-home")) {
    const syncNav = function () {
      if (window.scrollY > 40) {
        siteNav.classList.add("is-solid");
      } else {
        siteNav.classList.remove("is-solid");
      }
    };
    syncNav();
    window.addEventListener("scroll", syncNav, { passive: true });
  }

  const flashSuccess = document.body.dataset.flashSuccess;
  const flashError = document.body.dataset.flashError;
  if (window.Swal) {
    if (flashSuccess) {
      Swal.fire({
        icon: "success",
        title: "Done",
        text: flashSuccess,
        background: "#FAF6EE",
        color: "#241914",
        confirmButtonColor: "#B69A70"
      });
    }
    if (flashError) {
      Swal.fire({
        icon: "error",
        title: "Notice",
        text: flashError,
        background: "#FAF6EE",
        color: "#241914",
        confirmButtonColor: "#493326"
      });
    }
  }

  document.querySelectorAll("[data-confirm]").forEach(function (el) {
    el.addEventListener("click", function (event) {
      if (!window.Swal) {
        if (!window.confirm(el.dataset.confirm || "Are you sure?")) {
          event.preventDefault();
        }
        return;
      }
      event.preventDefault();
      const form = el.closest("form");
      Swal.fire({
        title: "Please confirm",
        text: el.dataset.confirm || "Are you sure?",
        icon: "warning",
        showCancelButton: true,
        background: "#FAF6EE",
        color: "#241914",
        confirmButtonColor: "#7a2e24",
        cancelButtonColor: "#816957",
        confirmButtonText: "Yes, continue"
      }).then(function (result) {
        if (result.isConfirmed && form) {
          form.submit();
        }
      });
    });
  });

  const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  if (!reduceMotion && window.gsap) {
    if (window.ScrollTrigger) {
      gsap.registerPlugin(ScrollTrigger);
    }

    const hero = document.querySelector(".hero-animate");
    if (hero) {
      gsap.fromTo(
        ".hero-animate > *",
        { opacity: 0, y: 28 },
        {
          opacity: 1,
          y: 0,
          duration: 0.95,
          stagger: 0.12,
          ease: "power3.out",
          clearProps: "opacity,transform"
        }
      );
    }

    if (window.ScrollTrigger) {
      gsap.utils.toArray(".reveal").forEach(function (section) {
        const bits = section.querySelectorAll(".section-head, .book-card, .collection-tile, .feature-visual, blockquote");
        if (!bits.length) {
          return;
        }
        gsap.from(bits, {
          opacity: 0,
          y: 28,
          duration: 0.75,
          stagger: 0.06,
          ease: "power2.out",
          scrollTrigger: {
            trigger: section,
            start: "top 82%"
          }
        });
      });
    }

    if (!document.querySelector(".hero")) {
      gsap.from(".book-card", {
        opacity: 0,
        y: 22,
        duration: 0.55,
        stagger: 0.05,
        ease: "power1.out",
        delay: 0.1
      });
    }
  }

  const bookType = document.getElementById("bookType");
  if (bookType) {
    const printedFields = document.getElementById("printedFields");
    const ebookFields = document.getElementById("ebookFields");
    const syncTypeFields = function () {
      const isEbook = bookType.value === "EBOOK";
      if (printedFields) printedFields.style.display = isEbook ? "none" : "block";
      if (ebookFields) ebookFields.style.display = isEbook ? "block" : "none";
    };
    bookType.addEventListener("change", syncTypeFields);
    syncTypeFields();
  }
})();
