/* ===================================================================
   PrepAgent AI Custom Premium JavaScript Engine
   =================================================================== */

document.addEventListener('DOMContentLoaded', () => {
    // Nav Navigation tabs
    initNavigation();
    
    // Form handlers
    initFormHandlers();
    
    // Result Tabs handlers
    initResultTabs();
    
    // Load placement history on startup
    loadHistoryList();
});

/* ===================================================================
   Navigation & Routing (SPA Tabs)
   =================================================================== */
function initNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');
    const tabContents = document.querySelectorAll('.tab-content');
    const mobileMenuBtn = document.getElementById('mobile-menu-btn');
    const navLinksContainer = document.querySelector('.nav-links');

    // Menu toggle for mobile screens
    mobileMenuBtn.addEventListener('click', () => {
        navLinksContainer.classList.toggle('mobile-open');
        const icon = mobileMenuBtn.querySelector('i');
        if (navLinksContainer.classList.contains('mobile-open')) {
            icon.className = 'fa-solid fa-xmark';
        } else {
            icon.className = 'fa-solid fa-bars';
        }
    });

    window.switchTab = function(tabId) {
        // Close mobile menu if open
        navLinksContainer.classList.remove('mobile-open');
        const icon = mobileMenuBtn.querySelector('i');
        if (icon) icon.className = 'fa-solid fa-bars';

        // Update Nav links
        navLinks.forEach(link => {
            if (link.getAttribute('data-tab') === tabId) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });

        // Toggle visibility of panels
        tabContents.forEach(content => {
            if (content.id === `${tabId}-section`) {
                content.classList.add('active');
            } else {
                content.classList.remove('active');
            }
        });

        // Auto scroll to top of viewport
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const tabId = link.getAttribute('data-tab');
            switchTab(tabId);
        });
    });

    document.getElementById('nav-logo-link').addEventListener('click', (e) => {
        e.preventDefault();
        switchTab('prepare');
    });
}

/* ===================================================================
   Form Handling & Validations
   =================================================================== */
function initFormHandlers() {
    const prepForm = document.getElementById('prep-form');
    const clearBtn = document.getElementById('clear-btn');
    
    // Select elements
    const studentNameInput = document.getElementById('studentName');
    const jobRoleSelect = document.getElementById('jobRole');
    const langSelect = document.getElementById('programmingLanguage');
    const topicInput = document.getElementById('topic');
    const difficultySelect = document.getElementById('difficultyLevel');

    // Validation helper
    function validateField(input, errorElementId, errorMsg) {
        const errorElement = document.getElementById(errorElementId);
        if (!input.value || input.value.trim() === '') {
            errorElement.textContent = errorMsg;
            input.style.borderColor = 'var(--error)';
            return false;
        } else {
            errorElement.textContent = '';
            input.style.borderColor = 'var(--border-glass)';
            return true;
        }
    }

    // Input listening validation triggers
    studentNameInput.addEventListener('input', () => validateField(studentNameInput, 'name-error', 'Student Name is required'));
    jobRoleSelect.addEventListener('change', () => validateField(jobRoleSelect, 'role-error', 'Please choose a job role'));
    langSelect.addEventListener('change', () => validateField(langSelect, 'lang-error', 'Please choose a programming language'));
    topicInput.addEventListener('input', () => validateField(topicInput, 'topic-error', 'Focus topic is required'));
    difficultySelect.addEventListener('change', () => validateField(difficultySelect, 'difficulty-error', 'Please choose a difficulty level'));

    // Clear form inputs
    clearBtn.addEventListener('click', () => {
        prepForm.reset();
        document.querySelectorAll('.error-msg').forEach(el => el.textContent = '');
        document.querySelectorAll('input, select').forEach(el => el.style.borderColor = 'var(--border-glass)');
        document.getElementById('plan-result-container').classList.add('hidden');
        showToast('Form cleared successfully', 'success');
    });

    // Form submit listener
    prepForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Perform validation check
        const isNameValid = validateField(studentNameInput, 'name-error', 'Student Name is required');
        const isRoleValid = validateField(jobRoleSelect, 'role-error', 'Please choose a job role');
        const isLangValid = validateField(langSelect, 'lang-error', 'Please choose a programming language');
        const isTopicValid = validateField(topicInput, 'topic-error', 'Focus topic is required');
        const isDifficultyValid = validateField(difficultySelect, 'difficulty-error', 'Please choose a difficulty level');

        if (!isNameValid || !isRoleValid || !isLangValid || !isTopicValid || !isDifficultyValid) {
            showToast('Please fix validation errors in the form', 'error');
            return;
        }

        const requestBody = {
            studentName: studentNameInput.value.trim(),
            jobRole: jobRoleSelect.value,
            programmingLanguage: langSelect.value,
            topic: topicInput.value.trim(),
            difficultyLevel: difficultySelect.value
        };

        // Trigger Loader overlay
        const loaderOverlay = document.getElementById('generation-loader');
        const progressFill = document.getElementById('loader-progress');
        loaderOverlay.classList.remove('hidden');
        progressFill.style.width = '0%';

        // Simulate progress bar increment
        let progress = 0;
        const progressInterval = setInterval(() => {
            if (progress < 90) {
                progress += Math.floor(Math.random() * 8) + 2;
                progressFill.style.width = `${Math.min(progress, 90)}%`;
            }
        }, 400);

        try {
            const response = await fetch('/api/preparation/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestBody)
            });

            clearInterval(progressInterval);
            progressFill.style.width = '100%';

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Server returned an error generating your preparation plan.');
            }

            const data = await response.json();
            
            // Allow progress bar to sit at 100% for brief instant
            setTimeout(() => {
                loaderOverlay.classList.add('hidden');
                renderPlanResult(data);
                showToast('Placement preparation plan generated successfully!', 'success');
                loadHistoryList(); // Reload history sidebar entries
            }, 600);

        } catch (error) {
            clearInterval(progressInterval);
            loaderOverlay.classList.add('hidden');
            console.error('Error generating plan:', error);
            showToast(error.message || 'Connection failed. Please verify API configurations or try again later.', 'error');
        }
    });
}

/* ===================================================================
   Result Display Tab Controller
   =================================================================== */
function initResultTabs() {
    const tabButtons = document.querySelectorAll('.res-tab-btn');
    const tabContents = document.querySelectorAll('.res-tab-content');

    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            tabButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            const tabId = btn.getAttribute('data-res-tab');
            tabContents.forEach(content => {
                if (content.id === `res-tab-${tabId}`) {
                    content.classList.add('active');
                } else {
                    content.classList.remove('active');
                }
            });
        });
    });
}

/* ===================================================================
   Result Rendering Engine
   =================================================================== */
function renderPlanResult(plan) {
    const resContainer = document.getElementById('plan-result-container');
    
    // Bind metadata
    document.getElementById('res-name').textContent = plan.studentName;
    document.getElementById('res-role').textContent = plan.jobRole;
    document.getElementById('res-lang').textContent = plan.programmingLanguage;
    document.getElementById('res-topic').textContent = plan.topic;
    
    const diffBadge = document.getElementById('res-difficulty');
    diffBadge.textContent = plan.difficulty;
    diffBadge.className = 'badge'; // Reset class list
    if (plan.difficulty.toLowerCase() === 'beginner') {
        diffBadge.classList.add('beginner');
    } else if (plan.difficulty.toLowerCase() === 'advanced') {
        diffBadge.classList.add('advanced');
    } else {
        diffBadge.classList.add('intermediate'); // default/medium
    }

    const createdDate = new Date(plan.createdDate);
    document.getElementById('res-date').textContent = createdDate.toLocaleString();

    const aiRes = plan.aiResponse;

    // 1. Render HR Questions
    renderAccordionList('hr-questions-container', aiRes.hrQuestions, 'Sample response approach: Highlight dynamic growth, state concrete values matching ' + plan.jobRole + ', and structure response using the STAR (Situation, Task, Action, Result) methodology.');

    // 2. Render Tech Questions
    renderAccordionList('tech-questions-container', aiRes.technicalQuestions, 'Correct Answer Outline: Focus on definitions, correct syntax representation in ' + plan.programmingLanguage + ', performance/complexity tradeoffs, and memory management considerations.');

    // 3. Render Coding Challenges
    renderCodingQuestions(aiRes.codingQuestions, plan.programmingLanguage);

    // 4. Render Aptitude Questions
    renderAptitudeQuestions(aiRes.aptitudeQuestions);

    // 5. Render Timeline Roadmap
    renderRoadmapTimeline(aiRes.learningRoadmap);

    // 6. Render Tips
    renderTipsList(aiRes.preparationTips);

    // Unhide results panel
    resContainer.classList.remove('hidden');
    
    // Auto scroll down to details
    resContainer.scrollIntoView({ behavior: 'smooth' });
}

// Accordion list renderer for HR and Tech questions
function renderAccordionList(containerId, questions, defaultHintText) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';

    if (!questions || questions.length === 0) {
        container.innerHTML = '<p class="text-muted">No questions generated.</p>';
        return;
    }

    questions.forEach((q, index) => {
        const card = document.createElement('div');
        card.className = 'question-card';

        card.innerHTML = `
            <div class="question-header">
                <div class="q-badge">${index + 1}</div>
                <div class="q-text">${escapeHTML(q)}</div>
                <i class="fa-solid fa-chevron-down toggle-arrow"></i>
            </div>
            <div class="answer-body">
                <h4><i class="fa-solid fa-lightbulb"></i> Response Guidance & Guidelines</h4>
                <p>${defaultHintText}</p>
            </div>
        `;

        const header = card.querySelector('.question-header');
        header.addEventListener('click', () => {
            card.classList.toggle('expanded');
        });

        container.appendChild(card);
    });
}

// Render Coding Cards with Solution blocks
function renderCodingQuestions(codingQuestions, lang) {
    const container = document.getElementById('coding-questions-container');
    container.innerHTML = '';

    if (!codingQuestions || codingQuestions.length === 0) {
        container.innerHTML = '<p class="text-muted">No coding challenges generated.</p>';
        return;
    }

    codingQuestions.forEach((q, index) => {
        const card = document.createElement('div');
        card.className = 'coding-card';

        const cardId = `coding-sol-${index}`;

        card.innerHTML = `
            <div class="coding-card-title">
                <h3>Challenge #${index + 1}: ${escapeHTML(q.title)}</h3>
                <span class="badge" style="background:rgba(99,102,241,0.1); color:var(--primary);">${escapeHTML(lang)}</span>
            </div>
            
            <div class="coding-desc-section">
                <h4>Problem Statement</h4>
                <p>${escapeHTML(q.problemStatement).replace(/\n/g, '<br>')}</p>
            </div>

            <div class="coding-desc-section">
                <h4>Constraints</h4>
                <div class="code-block" style="font-family: monospace;">${escapeHTML(q.constraints || 'Not Specified')}</div>
            </div>

            <div class="coding-io">
                <div class="coding-desc-section">
                    <h4>Sample Input</h4>
                    <pre class="code-block">${escapeHTML(q.sampleInput || 'N/A')}</pre>
                </div>
                <div class="coding-desc-section">
                    <h4>Sample Output</h4>
                    <pre class="code-block">${escapeHTML(q.sampleOutput || 'N/A')}</pre>
                </div>
            </div>

            <button class="sol-toggle-btn" data-toggle-target="${cardId}">
                <i class="fa-solid fa-code"></i> Show Reference Implementation & Explanation <i class="fa-solid fa-caret-down"></i>
            </button>

            <div class="coding-solution-container" id="${cardId}">
                <div class="coding-desc-section">
                    <h4>Reference Code (${escapeHTML(lang)})</h4>
                    <pre class="code-block" style="border-color: rgba(168,85,247,0.3);"><code style="color:#d8b4fe;">${escapeHTML(q.explanation.includes('class') || q.explanation.includes('def') || q.explanation.includes('function') ? q.explanation : '// Look below for detailed logic explanations')}\n\n// Solution Logic Details:\n${escapeHTML(q.explanation)}</code></pre>
                </div>
            </div>
        `;

        const toggleBtn = card.querySelector('.sol-toggle-btn');
        toggleBtn.addEventListener('click', () => {
            const targetContainer = card.querySelector('.coding-solution-container');
            targetContainer.classList.toggle('visible');
            const icon = toggleBtn.querySelector('i:last-child');
            if (targetContainer.classList.contains('visible')) {
                icon.className = 'fa-solid fa-caret-up';
                toggleBtn.style.color = 'var(--accent)';
            } else {
                icon.className = 'fa-solid fa-caret-down';
                toggleBtn.style.color = 'var(--secondary)';
            }
        });

        container.appendChild(card);
    });
}

// Render Interactive Aptitude quiz questions
function renderAptitudeQuestions(aptQuestions) {
    const container = document.getElementById('aptitude-questions-container');
    container.innerHTML = '';

    const scoreBadge = document.getElementById('quiz-score-badge');
    scoreBadge.textContent = 'Score: 0/5';
    let totalScore = 0;
    let questionsAnswered = 0;

    if (!aptQuestions || aptQuestions.length === 0) {
        container.innerHTML = '<p class="text-muted">No aptitude questions generated.</p>';
        return;
    }

    aptQuestions.forEach((q, index) => {
        const card = document.createElement('div');
        card.className = 'apt-question-block';

        const optButtonsHTML = q.options.map(opt => `
            <button class="apt-option" data-option-value="${escapeHTML(opt)}">
                <i class="fa-regular fa-circle"></i> <span>${escapeHTML(opt)}</span>
            </button>
        `).join('');

        card.innerHTML = `
            <div class="apt-q-title">
                <span style="color:var(--accent); font-weight:700;">Q${index + 1}.</span>
                <span>${escapeHTML(q.question)}</span>
            </div>
            <div class="apt-options">
                ${optButtonsHTML}
            </div>
            <div class="apt-explanation-box">
                <h5><i class="fa-solid fa-circle-info"></i> Solution & Formula</h5>
                <p>${escapeHTML(q.explanation)}</p>
            </div>
        `;

        const options = card.querySelectorAll('.apt-option');
        const explanationBox = card.querySelector('.apt-explanation-box');

        options.forEach(btn => {
            btn.addEventListener('click', () => {
                // If already answered, ignore
                if (card.classList.contains('answered')) return;
                
                card.classList.add('answered');
                questionsAnswered++;

                const selectedVal = btn.getAttribute('data-option-value');
                const correctVal = q.correctAnswer;

                options.forEach(b => {
                    const bVal = b.getAttribute('data-option-value');
                    const bIcon = b.querySelector('i');
                    
                    if (bVal === correctVal) {
                        b.classList.add('correct');
                        bIcon.className = 'fa-solid fa-circle-check';
                    }
                    if (bVal === selectedVal && selectedVal !== correctVal) {
                        b.classList.add('incorrect');
                        bIcon.className = 'fa-solid fa-circle-xmark';
                    }
                });

                if (selectedVal === correctVal) {
                    totalScore++;
                    scoreBadge.textContent = `Score: ${totalScore}/5`;
                    showToast(`Q${index + 1} Correct!`, 'success');
                } else {
                    showToast(`Q${index + 1} Incorrect.`, 'error');
                }

                // Show explanation
                explanationBox.classList.add('visible');

                // If all answered, show completion toast
                if (questionsAnswered === aptQuestions.length) {
                    setTimeout(() => {
                        showToast(`Aptitude Quiz Completed! Final Score: ${totalScore}/5`, 'success');
                    }, 1000);
                }
            });
        });

        container.appendChild(card);
    });
}

// Render Roadmap Timeline
function renderRoadmapTimeline(roadmap) {
    const container = document.getElementById('roadmap-timeline-container');
    container.innerHTML = '';

    if (!roadmap || roadmap.length === 0) {
        container.innerHTML = '<p class="text-muted">No roadmap generated.</p>';
        return;
    }

    roadmap.forEach(phase => {
        const item = document.createElement('div');
        item.className = 'timeline-item';

        const topicTagsHTML = phase.topics.map(t => `<span class="topic-tag">${escapeHTML(t)}</span>`).join('');

        item.innerHTML = `
            <div class="timeline-marker"></div>
            <div class="timeline-header">
                <h4>${escapeHTML(phase.phase)}</h4>
                <span class="duration-tag"><i class="fa-regular fa-clock"></i> ${escapeHTML(phase.duration)}</span>
            </div>
            <div class="timeline-topics">
                ${topicTagsHTML}
            </div>
            <div class="timeline-resources">
                <strong>Strategy & Core Tips:</strong> ${escapeHTML(phase.resourcesOrTips)}
            </div>
        `;

        container.appendChild(item);
    });
}

// Render Tips list
function renderTipsList(tips) {
    const container = document.getElementById('tips-container');
    container.innerHTML = '';

    if (!tips || tips.length === 0) {
        container.innerHTML = '<p class="text-muted">No preparation tips generated.</p>';
        return;
    }

    tips.forEach(tip => {
        const li = document.createElement('li');
        li.textContent = tip;
        container.appendChild(li);
    });
}

/* ===================================================================
   History Manager (CRUD calls)
   =================================================================== */
async function loadHistoryList() {
    const historyGrid = document.getElementById('history-items-grid');
    const emptyHistory = document.getElementById('empty-history');

    try {
        const response = await fetch('/api/preparation/history');
        if (!response.ok) throw new Error('Could not fetch history list');
        
        const historyList = await response.json();

        // Clear existing generated items, keep empty state template
        const items = historyGrid.querySelectorAll('.history-card');
        items.forEach(item => item.remove());

        if (historyList.length === 0) {
            emptyHistory.classList.remove('hidden');
            // Hide navbar history list count badge if any
            return;
        }

        emptyHistory.classList.add('hidden');

        historyList.forEach(item => {
            const card = document.createElement('div');
            card.className = 'history-card';

            const createdDate = new Date(item.createdDate);

            card.innerHTML = `
                <div class="hist-main-info">
                    <div class="hist-top-row">
                        <span class="badge" style="background:rgba(168,85,247,0.1); color:var(--secondary);">${escapeHTML(item.difficulty)}</span>
                        <span class="hist-date">${createdDate.toLocaleDateString()}</span>
                    </div>
                    <h3 class="hist-name">${escapeHTML(item.studentName)}</h3>
                    
                    <div class="hist-detail">
                        <i class="fa-solid fa-briefcase"></i>
                        <span>Role: ${escapeHTML(item.jobRole)}</span>
                    </div>
                    <div class="hist-detail">
                        <i class="fa-solid fa-code"></i>
                        <span>Language: ${escapeHTML(item.programmingLanguage)}</span>
                    </div>
                    <div class="hist-detail">
                        <i class="fa-solid fa-book"></i>
                        <span>Topic: ${escapeHTML(item.topic)}</span>
                    </div>
                </div>
                <div class="hist-actions">
                    <button class="btn btn-secondary btn-icon" data-view-id="${item.id}">
                        <i class="fa-solid fa-folder-open"></i> Load
                    </button>
                    <button class="btn btn-danger btn-icon" data-delete-id="${item.id}">
                        <i class="fa-solid fa-trash"></i> Delete
                    </button>
                </div>
            `;

            // Load event
            card.querySelector('[data-view-id]').addEventListener('click', () => {
                loadPlanDetails(item.id);
            });

            // Delete event
            card.querySelector('[data-delete-id]').addEventListener('click', (e) => {
                e.stopPropagation();
                deletePlanItem(item.id);
            });

            historyGrid.appendChild(card);
        });

    } catch (error) {
        console.error('Error fetching history:', error);
        showToast('Failed to fetch placement history.', 'error');
    }
}

async function loadPlanDetails(id) {
    showToast('Loading plan details...', 'success');
    
    try {
        const response = await fetch(`/api/preparation/history/${id}`);
        if (!response.ok) throw new Error('Could not fetch details for selected plan.');

        const data = await response.json();
        
        // Show in Generate/Prepare page result section
        switchTab('prepare');
        renderPlanResult(data);

    } catch (error) {
        console.error('Error fetching details:', error);
        showToast('Error loading preparation details.', 'error');
    }
}

async function deletePlanItem(id) {
    if (!confirm('Are you sure you want to delete this placement plan from history?')) return;

    try {
        const response = await fetch(`/api/preparation/history/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Could not delete selected plan.');

        showToast('Placement plan deleted successfully.', 'success');
        
        // Reload list
        loadHistoryList();
        
        // Hide result container if it matches current deleted item
        const resultContainer = document.getElementById('plan-result-container');
        if (!resultContainer.classList.contains('hidden')) {
            // A simple check is comparing header names (not foolproof, but safe UI reset)
            document.getElementById('plan-result-container').classList.add('hidden');
        }

    } catch (error) {
        console.error('Error deleting plan:', error);
        showToast('Error deleting preparation plan.', 'error');
    }
}

/* ===================================================================
   Notification/Toast Engine
   =================================================================== */
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    const icon = type === 'success' 
        ? '<i class="fa-solid fa-circle-check"></i>' 
        : '<i class="fa-solid fa-circle-exclamation"></i>';

    toast.innerHTML = `
        ${icon}
        <div class="toast-content">${escapeHTML(message)}</div>
    `;

    container.appendChild(toast);

    // Trigger reveal transition
    setTimeout(() => {
        toast.classList.add('show');
    }, 10);

    // Fade and remove
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            toast.remove();
        }, 400);
    }, 4000);
}

/* ===================================================================
   Helper Utility Methods
   =================================================================== */
function escapeHTML(str) {
    if (!str) return '';
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

window.printPlan = function() {
    window.print();
};
