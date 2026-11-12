(function () {
  const navToggle = document.getElementById("navToggle");
  const navLinks = document.getElementById("navLinks");
  if (navToggle && navLinks) {
    navToggle.addEventListener("click", function () {
      navLinks.classList.toggle("open");
      const expanded = navLinks.classList.contains("open");
      navToggle.setAttribute("aria-expanded", expanded ? "true" : "false");
    });
  }

  const flashSuccess = document.body.dataset.flashSuccess;
  const flashError = document.body.dataset.flashError;
  if (window.Swal) {
    if (flashSuccess) {
      Swal.fire({
        icon: "success",
        title: "Done",
        text: flashSuccess,
        confirmButtonColor: "#B69A70"
      });
    }
    if (flashError) {
      Swal.fire({
        icon: "error",
        title: "Notice",
        text: flashError,
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
    if (document.querySelector(".hero-animate")) {
      gsap.from(".hero-animate > *", {
        opacity: 0,
        y: 28,
        duration: 0.9,
        stagger: 0.12,
        ease: "power2.out"
      });
    }
    if (document.querySelector(".reveal")) {
      gsap.utils.toArray(".reveal").forEach(function (section) {
        gsap.from(section, {
          opacity: 0,
          y: 36,
          duration: 0.8,
          ease: "power2.out",
          scrollTrigger: section
        });
      });
    }
    gsap.from(".book-card", {
      opacity: 0,
      y: 20,
      duration: 0.55,
      stagger: 0.06,
      ease: "power1.out",
      delay: 0.15
    });
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
