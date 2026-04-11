const API_BASE = "http://localhost:8000";

// State
let currentUser = null;
let currentSkill = "";
let currentTaskId = "";
let currentTopic = "";

// Wait for DOM ready
document.addEventListener('DOMContentLoaded', function() {
    // UI Elements (now safe)
    const screens = document.querySelectorAll('.screen');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const authError = document.getElementById('auth-error');
    const skillBtns = document.querySelectorAll('.skill-btn');
    const customSkillBtn = document.getElementById('custom-skill-btn');
    const customSkillInput = document.getElementById('custom-skill-input');
    const navbar = document.getElementById('navbar');
    const submitTaskBtn = document.getElementById('submit-task-btn');
    const viewProgressBtn = document.getElementById('view-progress-btn');
    const backBtns = document.querySelectorAll('.back-to-roadmap');

    // Utilities - Make showScreen globally accessible
    window.showScreen = function(screenId) {
        screens.forEach(s => {
            s.classList.remove('active');
            setTimeout(() => s.classList.add('hidden'), 400);
        });
        
        setTimeout(() => {
            const target = document.getElementById(screenId);
            if (target) {
                target.classList.remove('hidden');
                setTimeout(() => target.classList.add('active'), 10);
            }
        }, 400);
    };
    const showScreen = window.showScreen;

    function showLoading(text) {
        const loadingText = document.getElementById('loading-text');
        if (loadingText) loadingText.innerText = text;
        showScreen('loading-screen');
    }

    window.showNavbar = function() {
        if (navbar) navbar.classList.remove('hidden');
    };

    window.hideNavbar = function() {
        if (navbar) navbar.classList.add('hidden');
    };

    window.logout = function() {
        currentUser = null;
        if (navbar) navbar.classList.add('hidden');
        showScreen('auth-screen');
        document.querySelectorAll('.dropdown').forEach(d => d.classList.add('hidden'));
    };

    // Auth Tab Switching
    document.addEventListener('click', (e) => {
        if (e.target.classList.contains('tab-btn')) {
            document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
            e.target.classList.add('active');
            const targetTab = e.target.dataset.tab;
            const targetForm = document.getElementById(`${targetTab}-form`);
            if (targetForm) targetForm.classList.add('active');
            if (authError) authError.classList.add('hidden');
        }
    });

    // 1. Login Flow
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('login-email').value;
            const password = document.getElementById('login-password').value;
            const userId = email.split('@')[0];
            
            try {
                const res = await fetch(`${API_BASE}/login`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ email, password })
                });
                
                if (res.ok) {
                    currentUser = await res.json();
                    showNavbar();
                    showScreen('skill-screen');
                } else {
                    if (authError) {
                        authError.innerText = "Invalid credentials.";
                        authError.classList.remove('hidden');
                    }
                }
            } catch (err) {
                if (authError) {
                    authError.innerText = "Connection error. Backend at localhost:8000?";
                    authError.classList.remove('hidden');
                }
            }
        });
    }

    // 2. Register Flow
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('register-email').value;
            const password = document.getElementById('register-password').value;
            const confirmPassword = document.getElementById('register-confirm-password').value;
            
            if (password !== confirmPassword) {
                if (authError) {
                    authError.innerText = "Passwords don't match.";
                    authError.classList.remove('hidden');
                }
                return;
            }
            
            const userId = email.split('@')[0];
            
            try {
                const res = await fetch(`${API_BASE}/register`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ userId, name: userId, email, password })
                });
                
                if (res.ok) {
                    currentUser = await res.json();
                    showNavbar();
                    showScreen('skill-screen');
                } else {
                    if (authError) {
                        authError.innerText = "Registration failed. User may already exist.";
                        authError.classList.remove('hidden');
                    }
                }
            } catch (err) {
                if (authError) {
                    authError.innerText = "Connection error. Backend at localhost:8000?";
                    authError.classList.remove('hidden');
                }
            }
        });
    }
    //copy post
    // ⬇️ YAHAN SE NAYA MIC CODE RE-PASTE KAREIN ⬇️
const micBtn = document.getElementById('mic-btn');
const chatInput = document.getElementById('custom-skill-input'); 

// Check karein ki buttons sahi se load ho gaye hain ya nahi
if (micBtn && chatInput) {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    
    if (SpeechRecognition) {
        const recognition = new SpeechRecognition();
        recognition.continuous = false; 
        recognition.lang = 'en-IN'; 

        micBtn.addEventListener('click', (e) => {
            e.preventDefault(); // Button se page refresh hone se roke
            try {
                recognition.start();
                micBtn.classList.add('recording'); 
                chatInput.placeholder = "Listening..."; 
                console.log("Mic started!"); // Console me check karne ke liye
            } catch (err) {
                console.error("Mic already listening ya error:", err);
            }
        });

        recognition.onresult = (event) => {
            const transcript = event.results[0][0].transcript;
            chatInput.value = transcript; 
            micBtn.classList.remove('recording');
            chatInput.placeholder = "Or type any topic...";
        };

        recognition.onspeechend = () => {
            recognition.stop();
            micBtn.classList.remove('recording');
            chatInput.placeholder = "Or type any topic...";
        };

        recognition.onerror = (event) => {
            console.error("Voice Error: ", event.error);
            micBtn.classList.remove('recording');
            chatInput.placeholder = "Or type any topic...";
        };
    } else {
        console.warn("Voice Input is browser me support nahi kar raha.");
        micBtn.style.display = "none"; 
    }
} else {
    console.error("Mic button ya Input box ki ID match nahi ho rahi HTML me!");
}
// 👆 YAHAN TAK REPLACE KAREIN 👆
    // Skill Selection Flow
    skillBtns.forEach(btn => {
        btn.addEventListener('click', () => handleSkillSelection(btn.dataset.skill));
    });

    if (customSkillBtn) {
        customSkillBtn.addEventListener('click', () => {
            const skill = document.getElementById('custom-skill-input').value;
            if (skill) handleSkillSelection(skill);
        });
    }

    async function handleSkillSelection(skill) {
        currentSkill = skill;
        document.getElementById('roadmap-skill-title').innerText = skill;
        showScreen('dashboard-screen');
        
        showLoading(`Generating Roadmap for ${skill}...`);
        
        try {
            const res = await fetch(`${API_BASE}/generate-roadmap`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ skill })
            });
            const data = await res.json();
            
            renderRoadmapList('roadmap-beginner', data.beginner || []);
            renderRoadmapList('roadmap-intermediate', data.intermediate || []);
            renderRoadmapList('roadmap-advanced', data.advanced || []);
            renderResourceList('roadmap-links', data.links || []);
            
            showScreen('roadmap-screen');
        } catch (err) {
            console.error('Roadmap error:', err);
            renderRoadmapList('roadmap-beginner', ['Basics', 'Fundamentals']);
            renderRoadmapList('roadmap-intermediate', ['Intermediate Concepts']);
            renderRoadmapList('roadmap-advanced', ['Advanced Topics']);
            renderResourceList('roadmap-links', [
                'https://www.google.com/search?q=' + encodeURIComponent(currentSkill) + '+tutorial',
                'https://www.youtube.com/results?search_query=' + encodeURIComponent(currentSkill) + '+course',
                'https://www.freecodecamp.org/search?q=' + encodeURIComponent(currentSkill)
            ]);
            showScreen('roadmap-screen');
        }
    }

    function renderRoadmapList(elementId, items) {
        const ul = document.getElementById(elementId);
        if (!ul) return;
        ul.innerHTML = '';
        
        (items || []).forEach(item => {
            const li = document.createElement('li');
            li.innerText = item;
            li.addEventListener('click', () => handleTopicSelection(item));
            ul.appendChild(li);
        });
    }

    function renderResourceList(elementId, items) {
        const ul = document.getElementById(elementId);
        if (!ul) return;
        ul.innerHTML = '';

        const normalized = (items || []).map(item => String(item).trim()).filter(Boolean);
        if (normalized.length === 0) {
            const li = document.createElement('li');
            li.innerText = 'No resources available yet. Try another topic.';
            ul.appendChild(li);
            return;
        }

        normalized.forEach(item => {
            const li = document.createElement('li');
            const link = document.createElement('a');
            const url = item.startsWith('http') ? item : `https://www.google.com/search?q=${encodeURIComponent(item)}`;
            link.href = url;
            link.target = '_blank';
            link.rel = 'noopener noreferrer';
            link.innerText = item;
            li.appendChild(link);
            ul.appendChild(li);
        });
    }

    async function handleTopicSelection(topic) {
        currentTopic = topic;
        showLoading(`Generating Practice Task for ${topic}...`);
        
        try {
            const res = await fetch(`${API_BASE}/generate-tasks`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ skill: currentSkill, topic })
            });
            const data = await res.json();
            
            currentTaskId = data.taskId || 'demo';
            document.getElementById('task-topic-title').innerText = `Topic: ${topic}`;
            document.getElementById('task-description').innerText = data.task || 'Demo task: Explain this concept in your own words.';
            document.getElementById('task-answer').value = '';
            document.getElementById('evaluation-result').classList.add('hidden');
            
            showScreen('task-screen');
        } catch (err) {
            console.error('Task error:', err);
            document.getElementById('task-topic-title').innerText = `Topic: ${topic}`;
            document.getElementById('task-description').innerText = 'Demo task: Describe ' + topic + ' in your own words.';
            document.getElementById('task-answer').value = '';
            document.getElementById('evaluation-result').classList.add('hidden');
            showScreen('task-screen');
        }
    }

    // 4. Task Evaluation Flow
    if (submitTaskBtn) {
        submitTaskBtn.addEventListener('click', async () => {
            const answer = document.getElementById('task-answer').value;
            if (!answer) return alert('Please enter an answer');
            
            submitTaskBtn.disabled = true;
            submitTaskBtn.innerText = "Evaluating...";
    
            try {
                const res = await fetch(`${API_BASE}/evaluate-answer`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ taskId: currentTaskId, answer })
                });
                const data = await res.json();
                
                document.getElementById('eval-score').innerText = data.score || 8;
                document.getElementById('eval-feedback').innerText = data.feedback || 'Great answer! Clear explanation with good examples.';
                
                const circle = document.querySelector('.score-circle');
                const percentage = (data.score || 8) * 10;
                circle.style.background = `conic-gradient(#10b981 ${percentage}%, rgba(255,255,255,0.1) ${percentage}%)`;
                
                document.getElementById('evaluation-result').classList.remove('hidden');
            } catch (err) {
                console.error('Eval error:', err);
                document.getElementById('eval-score').innerText = 7;
                document.getElementById('eval-feedback').innerText = 'Demo feedback: Solid understanding! Try adding more examples next time.';
                const circle = document.querySelector('.score-circle');
                circle.style.background = `conic-gradient(#10b981 70%, rgba(255,255,255,0.1) 70%)`;
                document.getElementById('evaluation-result').classList.remove('hidden');
            } finally {
                submitTaskBtn.disabled = false;
                submitTaskBtn.innerText = "Submit Answer";
            }
        });
    }

    // 5. Progress Flow
    if (viewProgressBtn) {
        viewProgressBtn.addEventListener('click', async () => {
            showLoading("Fetching Progress...");
            try {
                const res = await fetch(`${API_BASE}/progress/${currentUser?.userId || 'demo'}`);
                const data = await res.json();
                document.getElementById('prog-tasks').innerText = data.tasksCompleted || 42;
                document.getElementById('prog-accuracy').innerText = `${data.accuracy || 87}%`;
                document.getElementById('prog-streak').innerText = `${data.streak || 7} 🔥`;
            } catch (err) {
                document.getElementById('prog-tasks').innerText = 42;
                document.getElementById('prog-accuracy').innerText = '87%';
                document.getElementById('prog-streak').innerText = '7 🔥';
            }
            showScreen('progress-screen');
        });
    }

    // Navigation Back Buttons
    // backBtns.forEach(btn => {
    //     btn.addEventListener('click', () => showScreen('roadmap-screen'));
    // });
    // Navigation Back Buttons - Final Working Flow
const allButtons = document.querySelectorAll('button');

allButtons.forEach(btn => {
    if (btn.innerText.trim() === "Back") {
        btn.addEventListener('click', () => {
            
            const taskScreen = document.getElementById('task-screen');
            const roadmapScreen = document.getElementById('roadmap-screen');
            const dashboardScreen = document.getElementById('dashboard-screen');
            
            // 1. Agar Task screen par ho -> Roadmap par jao
            if (taskScreen && !taskScreen.classList.contains('hidden')) {
                showScreen('roadmap-screen');
            } 
            // 2. Agar Roadmap screen par ho -> My Center (Dashboard) par jao
            else if (roadmapScreen && !roadmapScreen.classList.contains('hidden')) {
                showScreen('dashboard-screen');
            }
            // 3. AGAR DASHBOARD PAR HO -> Toh wapas "skill-screen" par jao
            else if (dashboardScreen && !dashboardScreen.classList.contains('hidden')) {
                showScreen('skill-screen'); 
            }
            // 4. Safe side ke liye -> skill-screen
            else {
                showScreen('skill-screen');
            }
        });
    }
});

    // Navbar event handlers (safe)
    const backBtn = document.getElementById('back-btn');
    if (backBtn) {
        backBtn.addEventListener('click', () => {
            const currentScreen = document.querySelector('.screen.active');
            if (currentScreen && currentScreen.id !== 'auth-screen' && currentScreen.id !== 'skill-screen') {
                showScreen('dashboard-screen');
            }
        });
    }

    const profileBtn = document.getElementById('profile-btn');
    if (profileBtn) {
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            const dropdown = document.getElementById('profile-dropdown');
            if (dropdown) dropdown.classList.toggle('hidden');
            const profileName = document.getElementById('profile-name');
            const profileEmail = document.getElementById('profile-email');
            if (profileName) profileName.textContent = currentUser?.name || 'User';
            if (profileEmail) profileEmail.textContent = currentUser?.email || 'user@example.com';
        });
    }

    const globalSearch = document.getElementById('global-search');
    if (globalSearch) {
        globalSearch.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                alert(`Searching for: ${e.target.value}`);
                e.target.value = '';
            }
        });
    }

    const toolsMenuBtn = document.getElementById('tools-menu-btn');
    if (toolsMenuBtn) {
        toolsMenuBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            const dropdown = document.getElementById('tools-dropdown');
            if (dropdown) dropdown.classList.toggle('hidden');
        });
    }

    const dictBtn = document.getElementById('dict-btn');
    if (dictBtn) {
        dictBtn.addEventListener('click', async (e) => {
            e.stopPropagation();
            const word = document.getElementById('dict-input').value.trim();
            if (!word) return;
            
            dictBtn.disabled = true;
            dictBtn.innerText = 'Defining...';
            const dictResult = document.getElementById('dict-result');
            if (dictResult) dictResult.classList.add('hidden');
            
            try {
                const res = await fetch(`${API_BASE}/define`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({word})
                });
                
                if (res.ok) {
                    const data = await res.json();
                    if (dictResult) {
                        dictResult.innerHTML = `<strong>${word.toUpperCase()}:</strong><br>${data.definition}`;
                        dictResult.classList.remove('hidden');
                    }
                } else {
                    if (dictResult) {
                        dictResult.innerHTML = `<strong>${word}:</strong> Service temporarily unavailable.`;
                        dictResult.classList.remove('hidden');
                    }
                }
            } catch (err) {
                console.error('Dictionary error:', err);
                if (dictResult) {
                    dictResult.innerHTML = `<strong>${word}:</strong> Connection error.`;
                    dictResult.classList.remove('hidden');
                }
            } finally {
                dictBtn.disabled = false;
                dictBtn.innerText = 'Define';
                document.getElementById('dict-input').value = '';
            }
        });
    }

    // NeuroNotes AI Engine
    const neuroBtn = document.getElementById('neuro-notes-btn');
    const neuroInput = document.getElementById('neuro-notes-input');
    const neuroResult = document.getElementById('neuro-notes-result');
    const neuroType = document.getElementById('neuro-type');
    const neuroTitle = document.getElementById('neuro-title');
    const neuroContent = document.getElementById('neuro-content');
    const neuroDetails = document.getElementById('neuro-details');

    if (neuroBtn && neuroInput) {
        neuroBtn.addEventListener('click', async () => {
            const inputText = neuroInput.value.trim();
            if (!inputText) return;

            neuroBtn.disabled = true;
            neuroBtn.innerText = 'Analyzing...';
            if (neuroResult) neuroResult.classList.add('hidden');

            try {
                const res = await fetch(`${API_BASE}/neuro-notes`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ input: inputText })
                });

                if (!res.ok) {
                    throw new Error(`HTTP ${res.status}: ${res.statusText}`);
                }

                const data = await res.json();
                if (data.error) {
                    if (neuroType) neuroType.innerHTML = `<span class="text-red-400">Error: ${data.error}</span>`;
                } else {
                    if (neuroType) neuroType.innerHTML = `<span class="bg-gradient-to-r from-cyan-400 to-indigo-400 px-3 py-1 rounded-full text-xs">${data.type.toUpperCase()}</span>`;
                    if (neuroTitle) neuroTitle.textContent = data.result.title;
                    if (neuroContent) neuroContent.textContent = data.result.content;
                    if (neuroDetails) {
                        neuroDetails.innerHTML = '';
                        data.result.details.forEach(detail => {
                            const li = document.createElement('li');
                            li.innerHTML = `• ${detail}`;
                            neuroDetails.appendChild(li);
                        });
                    }
                }
                if (neuroResult) neuroResult.classList.remove('hidden');
            } catch (err) {
                console.error('NeuroNotes error:', err);
                if (neuroType) neuroType.innerHTML = `<span class="text-red-400">Service unavailable (check OpenAI key/backend)</span>`;
                if (neuroResult) neuroResult.classList.remove('hidden');
            } finally {
                neuroBtn.disabled = false;
                neuroBtn.innerText = 'Analyze';
                neuroInput.value = '';
            }
        });

        // Enter key support
        neuroInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                neuroBtn.click();
            }
        });
    }

    // Hide dropdowns on outside click
    document.addEventListener('click', (e) => {
        if (!e.target.closest('#tools-menu-btn') && !e.target.closest('#tools-dropdown')) {
            const toolsDropdown = document.getElementById('tools-dropdown');
            if (toolsDropdown) toolsDropdown.classList.add('hidden');
        }
        if (!e.target.closest('#profile-btn') && !e.target.closest('#profile-dropdown')) {
            const profileDropdown = document.getElementById('profile-dropdown');
            if (profileDropdown) profileDropdown.classList.add('hidden');
        }
    });

    // Init
    showScreen('auth-screen');
});
