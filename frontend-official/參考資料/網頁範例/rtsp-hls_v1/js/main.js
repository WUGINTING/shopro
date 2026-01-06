// Back to Top Button Functionality
document.addEventListener('DOMContentLoaded', function() {
    const backToTopButton = document.getElementById('backToTop');
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.getElementById('navMenu');
    const navLinks = document.querySelectorAll('.nav-link');
    
    // Hamburger menu toggle
    hamburger.addEventListener('click', function() {
        hamburger.classList.toggle('active');
        navMenu.classList.toggle('active');
    });
    
    // Close menu when clicking on a link
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
        });
    });
    
    // Close menu when clicking outside
    document.addEventListener('click', function(event) {
        const isClickInsideNav = navMenu.contains(event.target);
        const isClickOnHamburger = hamburger.contains(event.target);
        
        if (!isClickInsideNav && !isClickOnHamburger && navMenu.classList.contains('active')) {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
        }
    });
    
    // Show/hide button based on scroll position
    window.addEventListener('scroll', function() {
        if (window.pageYOffset > 300) {
            backToTopButton.classList.add('show');
        } else {
            backToTopButton.classList.remove('show');
        }
    });
    
    // Scroll to top when button is clicked
    backToTopButton.addEventListener('click', function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
    
    // Navigation active state
    const sections = document.querySelectorAll('[id]'); // 選擇所有有 id 的元素
    const navMenuLinks = document.querySelectorAll('.nav-menu a');
    
    window.addEventListener('scroll', function() {
        let current = '';
        
        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            const sectionHeight = section.clientHeight;
            if (window.pageYOffset >= sectionTop - 100) {
                const sectionId = section.getAttribute('id');
                // 只記錄導航連結中存在的 id
                if (sectionId && ['streaming', 'cases', 'how', 'plans', 'contact'].includes(sectionId)) {
                    current = sectionId;
                }
            }
        });
        
        navMenuLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href') === '#' + current) {
                link.classList.add('active');
            }
        });
    });
    
    // Contact Form Handling
    const contactForm = document.getElementById('contactForm');
    const formMessage = document.getElementById('formMessage');
    
    if (contactForm) {
        contactForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            // 獲取表單數據
            const formData = {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value,
                company: document.getElementById('company').value,
                message: document.getElementById('message').value
            };
            
            // 禁用提交按鈕
            const submitBtn = contactForm.querySelector('.btn-submit');
            const originalBtnText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            
            // 獲取當前語言的「發送中」文字
            const currentLang = localStorage.getItem('selectedLanguage') || 'tc';
            const langData = currentLang === 'en' ? en : tc;
            submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> ' + langData.contact.form.sending;
            
            // 隱藏之前的訊息
            formMessage.classList.remove('success', 'error');
            formMessage.style.display = 'none';
            
            try {
                // 使用 FormSubmit.co 免費服務發送郵件
                // 您需要先到 https://formsubmit.co/ 註冊您的郵件地址
                const response = await fetch('https://formsubmit.co/ajax/service.ginting@gmail.com', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify({
                        name: formData.name,
                        email: formData.email,
                        phone: formData.phone,
                        company: formData.company,
                        message: formData.message,
                        _subject: `新的聯絡表單訊息 - ${formData.name}`,
                        _template: 'table'
                    })
                });
                
                if (response.ok) {
                    // 成功
                    formMessage.textContent = langData.contact.form.success;
                    formMessage.classList.add('success');
                    formMessage.style.display = 'block';
                    contactForm.reset();
                } else {
                    throw new Error('發送失敗');
                }
            } catch (error) {
                // 失敗
                formMessage.textContent = langData.contact.form.error;
                formMessage.classList.add('error');
                formMessage.style.display = 'block';
            } finally {
                // 恢復提交按鈕
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            }
        });
    }
});
