// API Base URL
const API_BASE = '/api';

const CHATBOT_SYSTEM_PROMPT_STORAGE_KEY = 'chatbot.systemPrompt.v1';
const UI_THEME_STORAGE_KEY = 'ui.theme.v1';
const MACHINES_VIEW_STORAGE_KEY = 'ui.machinesView.v1';
const ASSESSMENTS_VIEW_STORAGE_KEY = 'ui.assessmentsView.v1';
const SIDEBAR_COLLAPSED_STORAGE_KEY = 'ui.sidebarCollapsed.v1';
const MACHINE_MANUAL_COMPLETION_STORAGE_KEY = 'machines.manualUploadById.v1';
const THEME_TOGGLE_SUN_ICON = '<svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><circle cx="12" cy="12" r="4.2" stroke="currentColor" stroke-width="1.8"></circle><path d="M12 2.5V5.2M12 18.8V21.5M4.5 12H1.8M22.2 12H19.5M5.9 5.9L7.8 7.8M18.1 18.1L16.2 16.2M18.1 5.9L16.2 7.8M5.9 18.1L7.8 16.2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>';
const THEME_TOGGLE_MOON_ICON = '<svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M20.1 14.7a8.8 8.8 0 1 1-10.8-10.8 7.1 7.1 0 0 0 10.8 10.8z" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>';
const ASSESSMENT_DETAILS_ICON = '<svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M12 5.5c4.9 0 8.72 4.03 9.55 5.01.12.15.18.32.18.49s-.06.34-.18.49c-.83.98-4.65 5.01-9.55 5.01S3.28 12.47 2.45 11.49a.77.77 0 0 1 0-.98C3.28 9.53 7.1 5.5 12 5.5Z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"></path><circle cx="12" cy="11" r="2.6" stroke="currentColor" stroke-width="1.7"></circle></svg>';
const ASSESSMENT_DELETE_ICON = '<svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M4.5 7.5h15M9.5 3.8h5l.7 1.7h3.2v1.9l-.9 10.2a2 2 0 0 1-1.99 1.82H8.49A2 2 0 0 1 6.5 17.6L5.6 7.4V5.5h3.2l.7-1.7Z" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path><path d="M10 10.2v5.1M14 10.2v5.1" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"></path></svg>';
const ASSESSMENT_EMPTY_STATE_ICON = '<svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M7 4.75h7.5L19 9.2v10.05a1.75 1.75 0 0 1-1.75 1.75h-10.5A1.75 1.75 0 0 1 5 19.25V6.5A1.75 1.75 0 0 1 6.75 4.75H7Z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"></path><path d="M14.5 4.75V9.2H19" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"></path><path d="M8.5 12.1h7M8.5 15.4h7" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path></svg>';
const DOCUMENT_FILE_ICON = '<svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M7 4.75h7.5L19 9.2v10.05A1.75 1.75 0 0 1 17.25 21h-10.5A1.75 1.75 0 0 1 5 19.25V6.5A1.75 1.75 0 0 1 6.75 4.75H7Z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"></path><path d="M14.5 4.75V9.2H19" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"></path><path d="M8.5 12.1h7M8.5 15.2h5.2" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path></svg>';
const DOCUMENT_EMPTY_STATE_ICON = '<svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M7 4.75h7.5L19 9.2v10.05A1.75 1.75 0 0 1 17.25 21h-10.5A1.75 1.75 0 0 1 5 19.25V6.5A1.75 1.75 0 0 1 6.75 4.75H7Z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"></path><path d="M14.5 4.75V9.2H19" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"></path><path d="M8.5 12.1h7M8.5 15.4h7" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path></svg>';
const DOCUMENT_ERROR_STATE_ICON = '<svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M12 4.75 19.5 18a1.15 1.15 0 0 1-.99 1.75H5.49A1.15 1.15 0 0 1 4.5 18L12 4.75Z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"></path><path d="M12 9.6v4.9M12 17.3h.01" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>';

// DOM Elements
let documentsTable, machinesGrid, assessmentsList, assessmentsVelocityChart, assessmentsVelocityMeta;
let assessmentHeatmapCanvas, assessmentHeatmapTooltip, assessmentHeatmapLegend, assessmentHeatmapEmpty, assessmentHeatmapWindowSelect;
let assessmentCadenceChart, assessmentCadenceEmpty, assessmentCadenceTargetLabel;
let dropZone, fileInput, uploadProgress, progressFill, uploadStatus;
let loadingOverlay, assessmentModal, assessmentDetails;
let machineManualUploadBtn, machineManualFileInput, machineManualStatus;
let machineForm, machineFormTitle, machineSubmitBtn, machineResetBtn, machineSearchInput, machineViewGridBtn, machineViewListBtn;
let assessmentViewGridBtn, assessmentViewScrollBtn, assessmentViewKanbanBtn;
let machineGridControls, machineGridZoomOutBtn, machineGridZoomInBtn, machineGridExpandBtn, machineGridInfo;
let assessmentGridControls, assessmentGridZoomOutBtn, assessmentGridZoomInBtn, assessmentGridExpandBtn, assessmentGridInfo;
let assessmentKanbanControls, assessmentKanbanLimitInput, assessmentKanbanApplyBtn;
let statDocumentsCount, statMachinesCount, statAssessmentsCount, statConformeCount;
let dashboardRiskChart, dashboardRecentList;
let chatbotMessages, chatbotComposer, chatbotInput, chatbotSendBtn, chatbotNewChatBtn, chatbotSubtitle, chatbotStatus, chatbotStatusLabel;
let chatbotSystemPrompt, chatbotPromptResetBtn, chatbotPromptStatus;
let chatbotCsvModal, chatbotCsvModalCloseBtn, chatbotCsvYesBtn, chatbotCsvNoBtn, chatbotCsvCancelBtn, chatbotExportStatus, chatbotEditStatus;
let appSidebar, sidebarToggleBtn, sidebarMobileToggleBtn, sidebarBackdrop, sidebarHomeLink;
let themeToggleBtn;
let toastRegion;
let confirmModal, confirmModalMessage, confirmModalOkBtn, confirmModalCancelBtn, confirmModalCloseBtn;
let chatCharCounter;

// State
let editingMachineId = null;
let machinesById = new Map();
let machinesCache = [];
let assessmentsCache = [];
let machineSearchQuery = '';
let machinesViewMode = 'scroll';
let assessmentsViewMode = 'scroll';
let machineGridExpanded = false;
let assessmentGridExpanded = false;
let machineGridZoomLevel = 1;
let assessmentGridZoomLevel = 1;
let assessmentKanbanVisibleLimit = 5;
let sidebarCollapsed = false;
let sidebarDrawerOpen = false;
let sidebarUiInitialized = false;
let machineManualUploadedInFormSession = false;
let machineManualCompletionById = {};

// Chat state
let currentAssessmentContext = null;
let assessmentChatThreads = new Map();
let chatbotThread = null;
let chatbotDefaultSystemPrompt = '';
let chatbotSystemPromptSaveTimer = null;
let chatbotExportStatusTimer = null;
let chatbotMessageEditingState = null;
let chatbotRequestInProgress = false;
let chatbotTypingEffects = new Map();
let chatbotCopyFeedbackTimers = new Map();
let chatbotConnectionState = 'online';
let chatbotRenderedMessageIds = new Set();
let confirmModalOnConfirm = null;
let confirmModalTrigger = null;

const SCROLL_REVEAL_SELECTOR = [
    '.section-header',
    '.dashboard-panel',
    '.quick-action-card',
    '.dashboard-activity-item',
    '.upload-area',
    '.upload-progress',
    '.documents-list',
    '.machines-list',
    '.assessments-list',
    '.card',
    '.machine-card',
    '.assessment-card',
    '.kanban-column',
    '.kanban-assessment-card',
    '.chatbot-shell',
    '.finding-item'
].join(',');

const STACK_CONTAINER_CONFIGS = [
    { containerSelector: '#machinesGrid', itemSelector: '.machine-card', overlap: 28, drift: 14 }
];

let scrollRevealObserver = null;
let scrollEffectFrame = null;
let scrollEffectContainers = [];
let navLinks = [];
let appSections = [];
let activeSectionId = '';
const SECTION_IDS = ['dashboard', 'documents', 'machines', 'chatbot', 'assessments'];
const DEFAULT_SECTION = 'dashboard';
const sectionScrollPositions = new Map();
const DASHBOARD_COUNTUP_DURATION_MS = 800;
const ASSESSMENT_KANBAN_COLUMNS = [
    { key: 'Basso', className: 'risk-basso' },
    { key: 'Medio', className: 'risk-medio' },
    { key: 'Alto', className: 'risk-alto' },
    { key: 'Critico', className: 'risk-critico' }
];
const ASSESSMENT_VELOCITY_WEEKS = 8;
const ASSESSMENT_GRID_ZOOM_MIN = 0.58;
const ASSESSMENT_GRID_ZOOM_MAX = 1.52;
const ASSESSMENT_GRID_ZOOM_STEP = 0.14;
const ASSESSMENT_KANBAN_PREVIEW_DEFAULT = 5;
const ASSESSMENT_KANBAN_PREVIEW_MIN = 1;
const ASSESSMENT_KANBAN_PREVIEW_MAX = 200;
const ASSESSMENT_VELOCITY_TOTAL_COLOR = '#1d4ed8';
const ASSESSMENT_VELOCITY_CONFORME_COLOR = '#15803d';
let sectionLeaveTimeoutId = null;
let sectionActivationFrameId = null;
let navigationTransitionToken = 0;
let dashboardCountupFrameId = null;
let dashboardCountupToken = 0;
let dashboardCountupPending = true;
let themeSwitchAnimationTimer = null;
let assessmentHeatmapWindowSize = 5;
let assessmentHeatmapResizeTimer = null;
let assessmentHeatmapCells = [];

const ASSESSMENT_HEATMAP_DEFAULT_WINDOW = 5;
const ASSESSMENT_HEATMAP_MIN_WINDOW = 1;
const ASSESSMENT_HEATMAP_MIN_CELL_SIZE = 28;
const ASSESSMENT_HEATMAP_MAX_CELL_SIZE = 52;
const ASSESSMENT_HEATMAP_CELL_GAP = 8;
const ASSESSMENT_CADENCE_TARGET_DAYS = 30;
const ASSESSMENT_CADENCE_TOLERANCE_FACTOR = 2;
const GRID_PREVIEW_LIMIT = 5;
const GRID_ZOOM_MIN = 0.7;
const GRID_ZOOM_MAX = 1.4;
const GRID_ZOOM_STEP = 0.12;
const MACHINES_GRID_BASE_MIN_WIDTH = 300;
const ASSESSMENTS_GRID_BASE_MIN_WIDTH = 360;
const CHATBOT_CHAR_WARNING_THRESHOLD = 1800;
const CHATBOT_CHAR_LIMIT = 2000;
const CHATBOT_TEXTAREA_MAX_HEIGHT = 180;

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    initElements();
    initSidebar();
    initThemeToggle();
    initAssessmentModalUi();
    initConfirmModalUi();
    initNavigation();
    initDashboardQuickActions();
    initFileUpload();
    initMachineForm();
    initMachineManualUpload();
    initMachineSearch();
    initMachineViewToggle();
    initAssessmentViewToggle();
    initAssessmentsListUi();
    initGridDisplayControls();
    initAssessmentHeatmapUi();
    initChatbotCsvModalUi();
    initChatbot();
    bindRippleToButtons(document);
    refreshDashboardStats();
    loadDocuments();
    loadMachines();
    loadAssessments();
    initScrollDrivenEffects();
});

function initElements() {
    documentsTable = document.getElementById('documentsBody');
    machinesGrid = document.getElementById('machinesGrid');
    assessmentsList = document.getElementById('assessmentsList');
    assessmentsVelocityChart = document.getElementById('assessmentsVelocityChart');
    assessmentsVelocityMeta = document.querySelector('#assessmentsVelocityCard .assessments-velocity-card__meta');
    assessmentHeatmapCanvas = document.getElementById('assessmentHeatmapCanvas');
    assessmentHeatmapTooltip = document.getElementById('assessmentHeatmapTooltip');
    assessmentHeatmapLegend = document.getElementById('assessmentHeatmapLegend');
    assessmentHeatmapEmpty = document.getElementById('assessmentHeatmapEmpty');
    assessmentHeatmapWindowSelect = document.getElementById('assessmentHeatmapWindowSelect');
    assessmentCadenceChart = document.getElementById('assessmentCadenceChart');
    assessmentCadenceEmpty = document.getElementById('assessmentCadenceEmpty');
    assessmentCadenceTargetLabel = document.getElementById('assessmentCadenceTargetLabel');
    dropZone = document.getElementById('dropZone');
    fileInput = document.getElementById('fileInput');
    uploadProgress = document.getElementById('uploadProgress');
    progressFill = document.getElementById('progressFill');
    uploadStatus = document.getElementById('uploadStatus');
    loadingOverlay = document.getElementById('loadingOverlay');
    assessmentModal = document.getElementById('assessmentModal');
    assessmentDetails = document.getElementById('assessmentDetails');
    machineManualUploadBtn = document.getElementById('machineManualUploadBtn');
    machineManualFileInput = document.getElementById('machineManualFileInput');
    machineManualStatus = document.getElementById('machineManualStatus');

    machineForm = document.getElementById('machineForm');
    machineFormTitle = document.getElementById('machineFormTitle');
    machineSubmitBtn = document.getElementById('machineSubmitBtn');
    machineResetBtn = document.getElementById('machineResetBtn');
    machineSearchInput = document.getElementById('machineSearchInput');
    machineViewGridBtn = document.getElementById('machineViewGridBtn');
    machineViewListBtn = document.getElementById('machineViewListBtn');
    assessmentViewGridBtn = document.getElementById('assessmentViewGridBtn');
    assessmentViewScrollBtn = document.getElementById('assessmentViewScrollBtn');
    assessmentViewKanbanBtn = document.getElementById('assessmentViewKanbanBtn');
    machineGridControls = document.getElementById('machineGridControls');
    machineGridZoomOutBtn = document.getElementById('machineGridZoomOutBtn');
    machineGridZoomInBtn = document.getElementById('machineGridZoomInBtn');
    machineGridExpandBtn = document.getElementById('machineGridExpandBtn');
    machineGridInfo = document.getElementById('machineGridInfo');
    assessmentGridControls = document.getElementById('assessmentGridControls');
    assessmentGridZoomOutBtn = document.getElementById('assessmentGridZoomOutBtn');
    assessmentGridZoomInBtn = document.getElementById('assessmentGridZoomInBtn');
    assessmentGridExpandBtn = document.getElementById('assessmentGridExpandBtn');
    assessmentGridInfo = document.getElementById('assessmentGridInfo');
    assessmentKanbanControls = document.getElementById('assessmentKanbanControls');
    assessmentKanbanLimitInput = document.getElementById('assessmentKanbanLimitInput');
    assessmentKanbanApplyBtn = document.getElementById('assessmentKanbanApplyBtn');
    statDocumentsCount = document.getElementById('statDocumentsCount');
    statMachinesCount = document.getElementById('statMachinesCount');
    statAssessmentsCount = document.getElementById('statAssessmentsCount');
    statConformeCount = document.getElementById('statConformeCount');
    dashboardRiskChart = document.getElementById('dashboardRiskChart');
    dashboardRecentList = document.getElementById('dashboardRecentList');
    machinesViewMode = readStoredMachinesViewPreference();
    applyMachinesViewMode(machinesViewMode, { persist: false, refreshLayout: false });
    assessmentsViewMode = readStoredAssessmentsViewPreference();
    applyAssessmentsViewMode(assessmentsViewMode, { persist: false, refreshLayout: false });

    chatbotMessages = document.getElementById('chatbotMessages');
    chatbotComposer = document.getElementById('chatbotComposer');
    chatbotInput = document.getElementById('chatbotInput');
    chatbotSendBtn = document.getElementById('chatbotSendBtn');
    chatbotNewChatBtn = document.getElementById('chatbotNewChatBtn');
    chatbotSubtitle = document.getElementById('chatbotSubtitle');
    chatbotStatus = document.getElementById('chatbotStatus');
    chatbotStatusLabel = document.getElementById('chatbotStatusLabel');
    chatCharCounter = document.getElementById('chatCharCounter');
    chatbotSystemPrompt = document.getElementById('chatbotSystemPrompt');
    chatbotPromptResetBtn = document.getElementById('chatbotPromptResetBtn');
    chatbotPromptStatus = document.getElementById('chatbotPromptStatus');
    chatbotCsvModal = document.getElementById('chatbotCsvModal');
    chatbotCsvModalCloseBtn = document.getElementById('chatbotCsvModalCloseBtn');
    chatbotCsvYesBtn = document.getElementById('chatbotCsvYesBtn');
    chatbotCsvNoBtn = document.getElementById('chatbotCsvNoBtn');
    chatbotCsvCancelBtn = document.getElementById('chatbotCsvCancelBtn');
    chatbotExportStatus = document.getElementById('chatbotExportStatus');
    chatbotEditStatus = document.getElementById('chatbotEditStatus');
    appSidebar = document.getElementById('appSidebar');
    sidebarToggleBtn = document.getElementById('sidebarToggleBtn');
    sidebarMobileToggleBtn = document.getElementById('sidebarMobileToggleBtn');
    sidebarBackdrop = document.getElementById('sidebarBackdrop');
    sidebarHomeLink = document.getElementById('sidebarHomeLink');
    themeToggleBtn = document.getElementById('themeToggleBtn');
    confirmModal = document.getElementById('confirmModal');
    confirmModalMessage = document.getElementById('confirmModalMessage');
    confirmModalOkBtn = document.getElementById('confirmModalOkBtn');
    confirmModalCancelBtn = document.getElementById('confirmModalCancelBtn');
    confirmModalCloseBtn = document.getElementById('confirmModalCloseBtn');
    machineManualCompletionById = loadMachineManualCompletionState();
}

function readStoredThemePreference() {
    try {
        const value = localStorage.getItem(UI_THEME_STORAGE_KEY);
        if (value === 'dark' || value === 'light') return value;
    } catch (_) {
        // ignore storage access issues
    }
    return '';
}

function readStoredMachinesViewPreference() {
    try {
        const value = localStorage.getItem(MACHINES_VIEW_STORAGE_KEY);
        if (value === 'grid' || value === 'scroll') return value;
        if (value === 'list') return 'scroll';
    } catch (_) {
        // ignore storage access issues
    }
    return 'scroll';
}

function readStoredAssessmentsViewPreference() {
    try {
        const value = localStorage.getItem(ASSESSMENTS_VIEW_STORAGE_KEY);
        if (value === 'grid' || value === 'scroll' || value === 'kanban') return value;
        if (value === 'list') return 'scroll';
    } catch (_) {
        // ignore storage access issues
    }
    return 'scroll';
}

function readStoredSidebarCollapsedPreference() {
    try {
        const value = localStorage.getItem(SIDEBAR_COLLAPSED_STORAGE_KEY);
        if (value === '1' || value === 'true') return true;
        if (value === '0' || value === 'false') return false;
    } catch (_) {
        // ignore storage access issues
    }
    return false;
}

function normalizeMachineIdKey(value) {
    const numericId = Number(value);
    if (!Number.isFinite(numericId)) return '';
    return String(Math.trunc(numericId));
}

function loadMachineManualCompletionState() {
    try {
        const raw = localStorage.getItem(MACHINE_MANUAL_COMPLETION_STORAGE_KEY);
        if (!raw) return {};

        const parsed = JSON.parse(raw);
        if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) return {};

        const normalized = {};
        Object.entries(parsed).forEach(([key, value]) => {
            const idKey = normalizeMachineIdKey(key);
            if (!idKey || value !== true) return;
            normalized[idKey] = true;
        });

        return normalized;
    } catch (_) {
        return {};
    }
}

function persistMachineManualCompletionState() {
    try {
        localStorage.setItem(MACHINE_MANUAL_COMPLETION_STORAGE_KEY, JSON.stringify(machineManualCompletionById));
    } catch (_) {
        // ignore storage access issues
    }
}

function hasMachineManualCompletion(machineId) {
    const idKey = normalizeMachineIdKey(machineId);
    if (!idKey) return false;
    return machineManualCompletionById[idKey] === true;
}

function setMachineManualCompletion(machineId, options = {}) {
    const idKey = normalizeMachineIdKey(machineId);
    if (!idKey) return false;
    if (machineManualCompletionById[idKey] === true) return true;

    machineManualCompletionById[idKey] = true;
    persistMachineManualCompletionState();

    if (options.refreshUi !== false) {
        renderMachinesFromState();
    }
    return true;
}

function resolvePreferredTheme() {
    const stored = readStoredThemePreference();
    if (stored) return stored;

    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        return 'dark';
    }
    return 'light';
}

function startThemeSwitchAnimation() {
    if (window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        return;
    }

    const root = document.documentElement;
    root.classList.add('theme-switching');
    if (themeSwitchAnimationTimer) {
        clearTimeout(themeSwitchAnimationTimer);
    }

    themeSwitchAnimationTimer = window.setTimeout(() => {
        root.classList.remove('theme-switching');
        themeSwitchAnimationTimer = null;
    }, 280);
}

function ensureThemeToggleLabel(labelText) {
    if (!themeToggleBtn) return;

    const nextLabel = String(labelText || 'Dark Mode');
    let labelEl = themeToggleBtn.querySelector('.theme-toggle__a11y-label');
    let iconsEl = themeToggleBtn.querySelector('.theme-toggle__icons');

    if (!labelEl || !iconsEl) {
        themeToggleBtn.textContent = '';

        iconsEl = document.createElement('span');
        iconsEl.className = 'theme-toggle__icons';
        iconsEl.setAttribute('aria-hidden', 'true');

        const sunIcon = document.createElement('span');
        sunIcon.className = 'theme-toggle__glyph theme-toggle__glyph--sun';
        sunIcon.innerHTML = THEME_TOGGLE_SUN_ICON;

        const moonIcon = document.createElement('span');
        moonIcon.className = 'theme-toggle__glyph theme-toggle__glyph--moon';
        moonIcon.innerHTML = THEME_TOGGLE_MOON_ICON;

        iconsEl.append(sunIcon, moonIcon);

        labelEl = document.createElement('span');
        labelEl.className = 'theme-toggle__a11y-label sr-only';

        themeToggleBtn.append(iconsEl, labelEl);
    }

    labelEl.textContent = nextLabel;
    themeToggleBtn.dataset.themeLabel = nextLabel;
}

function applyTheme(theme, options = {}) {
    const next = theme === 'dark' ? 'dark' : 'light';
    if (options.animate) {
        startThemeSwitchAnimation();
    }
    document.documentElement.setAttribute('data-theme', next);

    if (themeToggleBtn) {
        const isDark = next === 'dark';
        const nextModeLabel = isDark ? 'Light Mode' : 'Dark Mode';
        themeToggleBtn.classList.toggle('is-dark', isDark);
        themeToggleBtn.setAttribute('aria-pressed', String(isDark));
        themeToggleBtn.setAttribute('aria-label', isDark ? 'Attiva tema chiaro' : 'Attiva tema scuro');
        themeToggleBtn.title = nextModeLabel;
        ensureThemeToggleLabel(nextModeLabel);
    }

    if (options.persist) {
        try {
            localStorage.setItem(UI_THEME_STORAGE_KEY, next);
        } catch (_) {
            // ignore storage access issues
        }
    }
}

function initThemeToggle() {
    applyTheme(resolvePreferredTheme());
    if (!themeToggleBtn) return;

    themeToggleBtn.addEventListener('click', () => {
        const current = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
        const next = current === 'dark' ? 'light' : 'dark';
        applyTheme(next, { persist: true, animate: true });
    });
}

function isSidebarMobileViewport() {
    if (window.matchMedia) {
        return window.matchMedia('(max-width: 767px)').matches;
    }
    return window.innerWidth < 768;
}

function syncSidebarUiState() {
    const isMobile = isSidebarMobileViewport();
    const isCollapsedDesktop = sidebarCollapsed && !isMobile;
    const isDrawerOpen = sidebarDrawerOpen && isMobile;

    document.body.classList.toggle('sidebar-collapsed', isCollapsedDesktop);
    document.body.classList.toggle('sidebar-mobile-open', isDrawerOpen);

    if (appSidebar) {
        appSidebar.classList.toggle('is-collapsed', isCollapsedDesktop);
        appSidebar.classList.toggle('is-drawer-open', isDrawerOpen);
    }

    if (sidebarBackdrop) {
        if (isDrawerOpen) {
            sidebarBackdrop.removeAttribute('hidden');
        } else {
            sidebarBackdrop.setAttribute('hidden', '');
        }
        sidebarBackdrop.classList.toggle('is-visible', isDrawerOpen);
    }

    if (sidebarToggleBtn) {
        const toggleLabel = isMobile
            ? (isDrawerOpen ? 'Chiudi menu' : 'Apri menu')
            : (isCollapsedDesktop ? 'Espandi sidebar' : 'Comprimi sidebar');
        sidebarToggleBtn.setAttribute('aria-label', toggleLabel);
        sidebarToggleBtn.title = toggleLabel;
        sidebarToggleBtn.setAttribute('aria-pressed', String(isCollapsedDesktop));
        sidebarToggleBtn.setAttribute('aria-expanded', String(isDrawerOpen));
    }

    if (sidebarMobileToggleBtn) {
        const mobileToggleLabel = isDrawerOpen ? 'Chiudi menu' : 'Apri menu';
        sidebarMobileToggleBtn.setAttribute('aria-label', mobileToggleLabel);
        sidebarMobileToggleBtn.title = mobileToggleLabel;
        sidebarMobileToggleBtn.setAttribute('aria-expanded', String(isDrawerOpen));
        sidebarMobileToggleBtn.classList.toggle('is-open', isDrawerOpen);
    }
}

function setSidebarDrawerOpen(nextOpen) {
    const isMobile = isSidebarMobileViewport();
    sidebarDrawerOpen = Boolean(nextOpen) && isMobile;
    syncSidebarUiState();
}

function closeSidebarDrawer() {
    if (!sidebarDrawerOpen) return;
    setSidebarDrawerOpen(false);
}

function applySidebarCollapsedPreference(options = {}) {
    if (options.persist) {
        try {
            localStorage.setItem(SIDEBAR_COLLAPSED_STORAGE_KEY, sidebarCollapsed ? '1' : '0');
        } catch (_) {
            // ignore storage access issues
        }
    }
    syncSidebarUiState();
}

function toggleSidebar() {
    if (isSidebarMobileViewport()) {
        setSidebarDrawerOpen(!sidebarDrawerOpen);
        return;
    }
    sidebarCollapsed = !sidebarCollapsed;
    applySidebarCollapsedPreference({ persist: true });
}

function initSidebar() {
    sidebarCollapsed = readStoredSidebarCollapsedPreference();
    applySidebarCollapsedPreference({ persist: false });

    if (sidebarUiInitialized) {
        return;
    }
    sidebarUiInitialized = true;

    if (sidebarToggleBtn) {
        sidebarToggleBtn.addEventListener('click', toggleSidebar);
    }

    if (sidebarMobileToggleBtn) {
        sidebarMobileToggleBtn.addEventListener('click', () => {
            setSidebarDrawerOpen(!sidebarDrawerOpen);
        });
    }

    if (sidebarBackdrop) {
        sidebarBackdrop.addEventListener('click', () => {
            setSidebarDrawerOpen(false);
        });
    }

    if (sidebarHomeLink) {
        sidebarHomeLink.addEventListener('click', (event) => {
            event.preventDefault();
            navigateTo(DEFAULT_SECTION);
            closeSidebarDrawer();
        });
    }

    window.addEventListener('resize', () => {
        if (!isSidebarMobileViewport()) {
            sidebarDrawerOpen = false;
        }
        syncSidebarUiState();
    });

    document.addEventListener('keydown', (event) => {
        if (event.key !== 'Escape') return;
        closeSidebarDrawer();
    });
}

function ensureToastRegion() {
    if (toastRegion && document.body.contains(toastRegion)) {
        return toastRegion;
    }

    toastRegion = document.getElementById('toastRegion');
    if (toastRegion) {
        return toastRegion;
    }

    toastRegion = document.createElement('div');
    toastRegion.id = 'toastRegion';
    toastRegion.className = 'toast-region';
    toastRegion.setAttribute('aria-live', 'polite');
    toastRegion.setAttribute('aria-atomic', 'true');
    document.body.appendChild(toastRegion);
    return toastRegion;
}

function showToast(message, type = 'success', options = {}) {
    if (!message) return;

    const region = ensureToastRegion();
    if (!region) return;

    const toast = document.createElement('div');
    const variant = type === 'error' ? 'error' : 'success';
    const duration = Number.isFinite(options.duration) ? options.duration : 3200;

    toast.className = `toast toast-${variant}`;
    toast.setAttribute('role', 'status');
    toast.textContent = String(message);
    region.appendChild(toast);

    requestAnimationFrame(() => {
        toast.classList.add('is-visible');
    });

    let dismissed = false;
    const dismissToast = () => {
        if (dismissed) return;
        dismissed = true;
        toast.classList.remove('is-visible');
        window.setTimeout(() => toast.remove(), 220);
    };

    const timeout = window.setTimeout(dismissToast, Math.max(1600, duration));
    toast.addEventListener('click', () => {
        window.clearTimeout(timeout);
        dismissToast();
    }, { once: true });
}

function showConfirmModal(message, onConfirm) {
    const text = String(message || '').trim();
    if (!confirmModal || !confirmModalMessage) {
        if (!text) return;
        if (window.confirm(text) && typeof onConfirm === 'function') {
            Promise.resolve(onConfirm()).catch((error) => console.error('Confirm action error:', error));
        }
        return;
    }

    confirmModalOnConfirm = typeof onConfirm === 'function' ? onConfirm : null;
    confirmModalMessage.textContent = text;
    confirmModalOkBtn && (confirmModalOkBtn.disabled = false);
    confirmModalCancelBtn && (confirmModalCancelBtn.disabled = false);
    confirmModalCloseBtn && (confirmModalCloseBtn.disabled = false);
    confirmModal.classList.add('active');
    confirmModal.setAttribute('aria-hidden', 'false');
    confirmModalOkBtn?.focus();
}

function closeConfirmModal(options = {}) {
    if (!confirmModal) return;

    const restoreFocus = Boolean(options.restoreFocus);
    const trigger = confirmModalTrigger;
    confirmModalTrigger = null;
    confirmModalOnConfirm = null;
    confirmModal.classList.remove('active');
    confirmModal.setAttribute('aria-hidden', 'true');
    if (confirmModalMessage) {
        confirmModalMessage.textContent = '';
    }
    if (confirmModalOkBtn) confirmModalOkBtn.disabled = false;
    if (confirmModalCancelBtn) confirmModalCancelBtn.disabled = false;
    if (confirmModalCloseBtn) confirmModalCloseBtn.disabled = false;

    if (restoreFocus && trigger && document.body.contains(trigger)) {
        trigger.focus();
    }
}

function addRipple(event, element) {
    if (!element || element.disabled) return;

    const rect = element.getBoundingClientRect();
    const hasCoordinates = Number.isFinite(event?.clientX) && Number.isFinite(event?.clientY) && event?.detail !== 0;
    const rawX = hasCoordinates ? event.clientX - rect.left : rect.width / 2;
    const rawY = hasCoordinates ? event.clientY - rect.top : rect.height / 2;
    const x = Math.min(rect.width, Math.max(0, rawX));
    const y = Math.min(rect.height, Math.max(0, rawY));

    element.style.setProperty('--ripple-x', `${x}px`);
    element.style.setProperty('--ripple-y', `${y}px`);
    element.classList.remove('is-rippling');
    // Force reflow so animation reliably restarts on rapid clicks
    void element.offsetWidth;
    element.classList.add('is-rippling');
    window.setTimeout(() => element.classList.remove('is-rippling'), 520);

    const ripple = document.createElement('span');
    ripple.className = 'ripple';
    ripple.style.left = `${x}px`;
    ripple.style.top = `${y}px`;
    element.appendChild(ripple);

    window.setTimeout(() => ripple.remove(), 600);
}

function bindRippleToButtons(root = document) {
    if (!root || typeof root.querySelectorAll !== 'function') return;

    root.querySelectorAll('.btn').forEach((button) => {
        if (button.dataset.rippleBound === '1') return;
        button.dataset.rippleBound = '1';
        button.addEventListener('click', (event) => addRipple(event, button));
    });
}

function animateRowEnter(container, selector) {
    if (!container || !selector) return;

    container.querySelectorAll(selector).forEach((item) => {
        item.classList.add('row-enter');
        window.setTimeout(() => item.classList.remove('row-enter'), 300);
    });
}

function initAssessmentModalUi() {
    if (assessmentDetails) {
        assessmentDetails.addEventListener('click', (e) => {
            const sourceButton = e.target.closest?.('.source-btn');
            if (sourceButton) {
                const panelId = sourceButton.getAttribute('aria-controls');
                if (!panelId) return;

                const panel = document.getElementById(panelId);
                if (!panel) return;

                const isExpanded = sourceButton.getAttribute('aria-expanded') === 'true';
                sourceButton.setAttribute('aria-expanded', String(!isExpanded));
                panel.hidden = isExpanded;
                return;
            }

            const tabButton = e.target.closest?.('.assessment-tab');
            if (tabButton) {
                const layout = tabButton.closest('.assessment-layout');
                const panel = tabButton.getAttribute('data-panel');
                if (!layout || !panel) return;
                setAssessmentActivePanel(layout, panel);
                return;
            }

            const askButton = e.target.closest?.('.ask-assistant-btn');
            if (askButton) {
                const layout = askButton.closest('.assessment-layout');
                const question = askButton.getAttribute('data-question') || '';
                if (!layout || !question) return;
                setAssessmentActivePanel(layout, 'chat');
                prefillAssessmentChatInput(layout, question);
                return;
            }

            const suggestionButton = e.target.closest?.('.chat-suggestion');
            if (suggestionButton) {
                const layout = suggestionButton.closest('.assessment-layout');
                const question = suggestionButton.getAttribute('data-question') || '';
                if (!layout || !question) return;
                setAssessmentActivePanel(layout, 'chat');
                prefillAssessmentChatInput(layout, question);
                return;
            }

            const resetButton = e.target.closest?.('.chat-reset-btn');
            if (resetButton) {
                const layout = resetButton.closest('.assessment-layout');
                const assessmentId = Number(layout?.getAttribute('data-assessment-id'));
                if (!layout || !Number.isFinite(assessmentId)) return;

                if (!confirm('Vuoi cancellare la chat per questo assessment?')) return;
                resetAssessmentChatThread(assessmentId);
                renderAssessmentChatIntoLayout(layout, assessmentId);
                return;
            }
        });

        assessmentDetails.addEventListener('submit', (e) => {
            const form = e.target.closest?.('.chat-composer');
            if (!form) return;
            e.preventDefault();
            submitAssessmentChat(form).catch((err) => console.error('Chat submit error:', err));
        });

        assessmentDetails.addEventListener('keydown', (e) => {
            const input = e.target.closest?.('.chat-input');
            if (!input) return;

            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                input.closest('form')?.requestSubmit();
            }
        });

        assessmentDetails.addEventListener('input', (e) => {
            const input = e.target.closest?.('.chat-input');
            if (!input) return;
            autoResizeChatInput(input);
        });
    }

    if (assessmentModal) {
        assessmentModal.addEventListener('click', (e) => {
            if (e.target === assessmentModal) closeModal();
        });
    }

    document.addEventListener('keydown', (e) => {
        if (e.key !== 'Escape') return;
        if (!assessmentModal?.classList.contains('active')) return;
        closeModal();
    });
}

function initConfirmModalUi() {
    if (!confirmModal) return;

    confirmModal.addEventListener('click', (event) => {
        if (event.target === confirmModal) {
            closeConfirmModal({ restoreFocus: true });
        }
    });

    confirmModalCloseBtn?.addEventListener('click', () => {
        closeConfirmModal({ restoreFocus: true });
    });

    confirmModalCancelBtn?.addEventListener('click', () => {
        closeConfirmModal({ restoreFocus: true });
    });

    confirmModalOkBtn?.addEventListener('click', async () => {
        const action = confirmModalOnConfirm;

        if (confirmModalOkBtn) confirmModalOkBtn.disabled = true;
        if (confirmModalCancelBtn) confirmModalCancelBtn.disabled = true;
        if (confirmModalCloseBtn) confirmModalCloseBtn.disabled = true;

        try {
            if (typeof action === 'function') {
                await action();
            }
        } catch (error) {
            console.error('Confirm modal action error:', error);
        } finally {
            closeConfirmModal({ restoreFocus: true });
        }
    });

    document.addEventListener('keydown', (event) => {
        if (event.key !== 'Escape') return;
        if (!confirmModal.classList.contains('active')) return;
        closeConfirmModal({ restoreFocus: true });
    });
}

function initAssessmentsListUi() {
    if (!assessmentsList || assessmentsList.dataset.actionsBound === '1') return;

    assessmentsList.dataset.actionsBound = '1';
    assessmentsList.addEventListener('click', handleAssessmentsListClick);
}

function handleAssessmentsListClick(event) {
    const button = event.target.closest?.('.delete-assessment-btn');
    if (!button || !assessmentsList?.contains(button)) return;

    event.preventDefault();

    const id = Number(button.dataset.id);
    const name = String(button.dataset.name || 'Macchinario sconosciuto').trim() || 'Macchinario sconosciuto';
    if (!Number.isFinite(id)) {
        showToast('Assessment non valido.', 'error');
        return;
    }

    confirmModalTrigger = button;
    showConfirmModal(
        `Sei sicuro di voler eliminare l'assessment del macchinario "${name}"? L'operazione e irreversibile.`,
        async () => {
            try {
                const response = await fetch(`${API_BASE}/assessments/${id}`, {
                    method: 'DELETE'
                });
                if (!response.ok) {
                    throw new Error('Errore eliminazione');
                }
                showToast('Assessment eliminato con successo', 'success');
                await loadAssessments();
            } catch (error) {
                console.error('Error deleting assessment:', error);
                showToast('Errore durante l\'eliminazione', 'error');
            }
        }
    );
}

// Navigation
function initNavigation() {
    navLinks = Array.from(document.querySelectorAll('.nav-link'));
    appSections = Array.from(document.querySelectorAll('.section'));

    if (!appSections.length) return;

    navLinks.forEach((link) => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const sectionId = extractSectionIdFromLink(link);
            navigateTo(sectionId);
            closeSidebarDrawer();
        });
    });

    const initialHashSectionId = parseSectionIdFromHash(window.location.hash);
    if (SECTION_IDS.includes(initialHashSectionId)) {
        navigateTo(initialHashSectionId, { pushState: false });
    } else {
        navigateTo(DEFAULT_SECTION, { pushState: false });
    }

    window.addEventListener('popstate', (event) => {
        const stateSectionId = event.state?.sectionId;
        const hashSectionId = parseSectionIdFromHash(window.location.hash);
        const nextSectionId = stateSectionId || hashSectionId || DEFAULT_SECTION;
        navigateTo(nextSectionId, { pushState: false, restoreScroll: true });
        closeSidebarDrawer();
    });
}

function initDashboardQuickActions() {
    document.querySelectorAll('[data-quick-nav]').forEach((button) => {
        if (button.dataset.quickNavBound === '1') return;
        button.dataset.quickNavBound = '1';

        button.addEventListener('click', () => {
            const sectionId = String(button.getAttribute('data-quick-nav') || '').trim();
            if (!sectionId) return;
            navigateTo(sectionId);
        });
    });
}

function extractSectionIdFromLink(link) {
    if (!link) return '';

    const fromData = String(link.getAttribute('data-section') || '').trim();
    if (fromData) return fromData;

    const href = String(link.getAttribute('href') || '').trim();
    if (!href) return '';
    if (href.startsWith('#')) return href.slice(1).trim();

    const hashIndex = href.indexOf('#');
    if (hashIndex === -1) return '';
    return href.slice(hashIndex + 1).trim();
}

function parseSectionIdFromHash(hashValue) {
    return String(hashValue || '').replace(/^#/, '').trim();
}

function scrollToInstant(topValue) {
    const safeTop = Number.isFinite(topValue) ? Math.max(0, topValue) : 0;
    try {
        window.scrollTo({ top: safeTop, behavior: 'instant' });
    } catch (_) {
        window.scrollTo(0, safeTop);
    }
}

function updateActiveNavLink(sectionId) {
    navLinks.forEach((link) => {
        const linkSectionId = extractSectionIdFromLink(link);
        link.classList.toggle('active', linkSectionId === sectionId);
    });
}

function navigateTo(sectionId, { pushState = true, restoreScroll = false } = {}) {
    const resolvedSectionId = SECTION_IDS.includes(sectionId) ? sectionId : DEFAULT_SECTION;

    const domActiveSectionId = document.querySelector('.section.active')?.id || '';
    const currentSectionId = activeSectionId || domActiveSectionId;
    if (currentSectionId) {
        activeSectionId = currentSectionId;
    }

    if (currentSectionId === resolvedSectionId) return;

    if (currentSectionId) {
        sectionScrollPositions.set(currentSectionId, window.scrollY);
    }
    if (currentSectionId === DEFAULT_SECTION && currentSectionId !== resolvedSectionId) {
        cancelDashboardCountupAnimation();
    }

    const currentSection = currentSectionId
        ? appSections.find((section) => section.id === currentSectionId) || document.getElementById(currentSectionId)
        : null;
    const targetSection = appSections.find((section) => section.id === resolvedSectionId)
        || document.getElementById(resolvedSectionId);
    if (!targetSection) return;

    if (sectionLeaveTimeoutId !== null) {
        clearTimeout(sectionLeaveTimeoutId);
        sectionLeaveTimeoutId = null;
    }
    if (sectionActivationFrameId !== null) {
        cancelAnimationFrame(sectionActivationFrameId);
        sectionActivationFrameId = null;
    }

    const transitionToken = ++navigationTransitionToken;
    const activateTargetSection = () => {
        sectionActivationFrameId = requestAnimationFrame(() => {
            sectionActivationFrameId = null;
            if (transitionToken !== navigationTransitionToken) return;

            targetSection.classList.remove('is-leaving');
            targetSection.classList.add('active');
            activeSectionId = resolvedSectionId;

            updateActiveNavLink(resolvedSectionId);

            if (pushState) {
                history.pushState({ sectionId: resolvedSectionId }, '', `#${resolvedSectionId}`);
            }

            if (restoreScroll && sectionScrollPositions.has(resolvedSectionId)) {
                scrollToInstant(sectionScrollPositions.get(resolvedSectionId));
            } else {
                scrollToInstant(0);
            }

            if (resolvedSectionId === DEFAULT_SECTION) {
                dashboardCountupPending = true;
                refreshDashboardStats();
            }

            refreshScrollDrivenEffects();
        });
    };

    if (currentSection) {
        currentSection.classList.add('is-leaving');
        sectionLeaveTimeoutId = window.setTimeout(() => {
            sectionLeaveTimeoutId = null;
            if (transitionToken !== navigationTransitionToken) return;
            currentSection.classList.remove('active', 'is-leaving');
            activateTargetSection();
        }, 200);
    } else {
        activateTargetSection();
    }
}

function initScrollDrivenEffects() {
    if (initScrollDrivenEffects.initialized) return;
    initScrollDrivenEffects.initialized = true;

    setupScrollRevealObserver();
    refreshScrollDrivenEffects();

    window.addEventListener('scroll', queueScrollEffectsFrame, { passive: true });
    window.addEventListener('resize', queueScrollEffectsFrame);
}

function setupScrollRevealObserver() {
    if (scrollRevealObserver) return;
    scrollRevealObserver = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            entry.target.classList.add('is-revealed');
        });
    }, {
        threshold: 0.15,
        rootMargin: '0px 0px -8% 0px'
    });
}

function refreshScrollDrivenEffects() {
    observeScrollRevealElements();
    prepareStackContainers();
    queueScrollEffectsFrame();
}

function observeScrollRevealElements() {
    if (!scrollRevealObserver) return;
    const activeSection = document.querySelector('.section.active');
    const targets = (activeSection || document).querySelectorAll(SCROLL_REVEAL_SELECTOR);

    targets.forEach((el) => {
        if (el.dataset.scrollRevealBound === '1') return;
        el.dataset.scrollRevealBound = '1';
        el.classList.add('scroll-reveal');
        scrollRevealObserver.observe(el);
    });
}

function prepareStackContainers() {
    clearStackContainerState();
    scrollEffectContainers = [];
    const activeSection = document.querySelector('.section.active');
    if (!activeSection) return;

    STACK_CONTAINER_CONFIGS.forEach((config) => {
        const isMachinesContainer = config.containerSelector === '#machinesGrid';
        if (isMachinesContainer && machinesViewMode !== 'scroll') {
            return;
        }
        const isAssessmentsContainer = config.containerSelector === '#assessmentsList';
        if (isAssessmentsContainer && assessmentsViewMode !== 'scroll') {
            return;
        }

        const container = activeSection.querySelector(config.containerSelector);
        if (!container) return;

        const items = Array.from(container.querySelectorAll(config.itemSelector))
            .filter((item) => !item.classList.contains('empty-state'));

        if (items.length < 2) return;

        container.setAttribute('data-stack-container', 'true');
        container.style.setProperty('--stack-overlap', `${config.overlap}px`);
        container.style.setProperty('--stack-drift', `${config.drift}px`);

        items.forEach((item, index) => {
            item.classList.add('stack-item');
            item.dataset.stackIndex = String(index);
        });

        scrollEffectContainers.push({
            items,
            drift: config.drift
        });
    });
}

function clearStackContainerState() {
    document.querySelectorAll('[data-stack-container]').forEach((container) => {
        container.removeAttribute('data-stack-container');
    });

    document.querySelectorAll('.stack-item').forEach((item) => {
        item.classList.remove('stack-item');
        item.classList.remove('is-stack-focused');
        item.removeAttribute('data-stack-index');
        item.style.removeProperty('--stack-translate-x');
        item.style.removeProperty('--stack-translate-y');
        item.style.removeProperty('--stack-scale');
        item.style.removeProperty('--stack-opacity');
        item.style.removeProperty('--stack-z');
        item.style.removeProperty('--stack-glow-opacity');
    });
}

function queueScrollEffectsFrame() {
    if (scrollEffectFrame !== null) return;
    scrollEffectFrame = requestAnimationFrame(() => {
        scrollEffectFrame = null;
        applyStackTransforms();
    });
}

function applyStackTransforms() {
    if (!scrollEffectContainers.length) return;

    const prefersReducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const viewportHeight = Math.max(window.innerHeight || 0, 1);
    const focusLine = viewportHeight * 0.56;

    scrollEffectContainers.forEach((entry) => {
        entry.items.forEach((item, index) => {
            if (prefersReducedMotion) {
                item.style.setProperty('--stack-translate-x', '0px');
                item.style.setProperty('--stack-translate-y', '0px');
                item.style.setProperty('--stack-scale', '1');
                item.style.setProperty('--stack-opacity', '1');
                item.style.setProperty('--stack-z', String(entry.items.length - index));
                item.style.setProperty('--stack-glow-opacity', '0');
                item.classList.remove('is-stack-focused');
                return;
            }

            // Scroll-driven "deck": ogni card regola profondita, traslazione e z-index in base alla posizione nel viewport.
            const rect = item.getBoundingClientRect();
            const center = rect.top + (rect.height / 2);
            const distance = (center - focusLine) / viewportHeight;
            const clamped = Math.max(-1, Math.min(1, distance));
            const focus = 1 - Math.min(1, Math.abs(clamped) * 1.45);

            const translateX = (index % 2 === 0 ? -1 : 1) * (1 - focus) * entry.drift;
            const translateY = clamped * 16;
            const scale = 0.955 + (focus * 0.055);
            const opacity = 0.85 + (focus * 0.15);
            const zIndex = Math.round(focus * 48) + (entry.items.length - index);
            const glowOpacity = 0.06 + (focus * 0.24);

            item.style.setProperty('--stack-translate-x', `${translateX.toFixed(2)}px`);
            item.style.setProperty('--stack-translate-y', `${translateY.toFixed(2)}px`);
            item.style.setProperty('--stack-scale', scale.toFixed(3));
            item.style.setProperty('--stack-opacity', opacity.toFixed(3));
            item.style.setProperty('--stack-z', String(zIndex));
            item.style.setProperty('--stack-glow-opacity', glowOpacity.toFixed(3));
            item.classList.toggle('is-stack-focused', focus > 0.78);
        });
    });
}

// File Upload
function initFileUpload() {
    if (!dropZone || !fileInput) return;

    let dragDepth = 0;

    // Drag and drop events
    dropZone.addEventListener('dragenter', (e) => {
        e.preventDefault();
        dragDepth += 1;
        dropZone.classList.add('dragover');
    });

    dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropZone.classList.add('dragover');
        if (e.dataTransfer) {
            e.dataTransfer.dropEffect = 'copy';
        }
    });

    dropZone.addEventListener('dragleave', (e) => {
        e.preventDefault();
        dragDepth = Math.max(0, dragDepth - 1);
        if (dragDepth === 0) {
            dropZone.classList.remove('dragover');
        }
    });

    dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        dragDepth = 0;
        dropZone.classList.remove('dragover');
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            uploadFile(files[0]);
        }
    });

    // File input change
    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            uploadFile(fileInput.files[0]);
        }
    });
}

async function uploadFile(file) {
    const fileName = String(file?.name || '');
    const mimeType = String(file?.type || '').toLowerCase();
    const isPdfMime = mimeType === 'application/pdf' || mimeType === 'application/x-pdf';
    const isPdfByName = fileName.toLowerCase().endsWith('.pdf');
    if (!isPdfMime && !isPdfByName) {
        alert('Solo file PDF sono accettati');
        return;
    }

    showPendingUploadPreview(file);

    const formData = new FormData();
    formData.append('file', file);

    uploadProgress.style.display = 'block';
    progressFill.style.width = '0%';
    uploadStatus.textContent = `${getDocumentFileIcon(fileName)} ${fileName} - Caricamento in corso...`;

    let progressInterval = null;

    try {
        // Simulate progress
        let progress = 0;
        progressInterval = setInterval(() => {
            progress += 10;
            if (progress <= 90) {
                progressFill.style.width = progress + '%';
            }
        }, 200);

        const response = await fetch(`${API_BASE}/documents/upload`, {
            method: 'POST',
            body: formData
        });

        progressFill.style.width = '100%';

        if (response.ok) {
            const result = await response.json();
            uploadStatus.textContent = `✓ ${fileName} caricato con successo! ${result.chunkCount} chunks creati.`;
            removePendingUploadPreview();
            await loadDocuments();

            setTimeout(() => {
                uploadProgress.style.display = 'none';
            }, 3000);
        } else {
            const error = await response.json().catch(() => ({}));
            const errorMessage = error?.error || 'Errore durante il caricamento';
            uploadStatus.textContent = `✗ Errore: ${errorMessage}`;
            setPendingUploadPreviewError(errorMessage);
        }
    } catch (error) {
        console.error('Upload error:', error);
        uploadStatus.textContent = `✗ Errore durante il caricamento`;
        setPendingUploadPreviewError('Upload non riuscito');
    } finally {
        if (progressInterval) {
            clearInterval(progressInterval);
        }
        if (fileInput) {
            fileInput.value = '';
        }
    }
}

function getDocumentFileVariant(fileName = '') {
    const extension = String(fileName).split('.').pop()?.toLowerCase() || '';
    if (extension === 'pdf') return 'pdf';
    if (['doc', 'docx', 'odt', 'txt'].includes(extension)) return 'text';
    if (['xls', 'xlsx', 'csv'].includes(extension)) return 'sheet';
    if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'].includes(extension)) return 'image';
    if (['zip', 'rar', '7z', 'tar', 'gz'].includes(extension)) return 'archive';
    return 'generic';
}

function buildDocumentNameCell(fileName = '') {
    const safeName = escapeHtml(fileName || 'Documento senza nome');
    const iconVariant = getDocumentFileVariant(fileName);
    return `
        <div class="doc-name">
            <span class="doc-file-icon doc-file-icon--${iconVariant}" aria-hidden="true">${DOCUMENT_FILE_ICON}</span>
            <strong class="doc-file-name" title="${safeName}">${safeName}</strong>
        </div>
    `;
}

function removePendingUploadPreview() {
    documentsTable?.querySelector('tr[data-pending-upload="true"]')?.remove();
}

function showPendingUploadPreview(file) {
    if (!documentsTable) return;

    removePendingUploadPreview();

    const emptyStateRow = documentsTable.querySelector('td.empty-state')?.closest('tr');
    emptyStateRow?.remove();

    const nowIsoDate = new Date().toISOString();
    const previewRowMarkup = `
        <tr class="upload-preview-row" data-pending-upload="true">
            <td data-label="Nome File" class="doc-name-cell">
                ${buildDocumentNameCell(file?.name || 'Documento in upload')}
            </td>
            <td data-label="Dimensione">${formatFileSize(file?.size || 0)}</td>
            <td data-label="Data Caricamento">${formatDate(nowIsoDate)}</td>
            <td data-label="Stato" data-preview-status>
                <span class="badge badge-warning">In caricamento</span>
            </td>
            <td data-label="Chunks">-</td>
            <td data-label="Azioni" class="actions-cell" data-preview-actions>
                <span class="upload-preview-note">Upload in corso...</span>
            </td>
        </tr>
    `;
    documentsTable.insertAdjacentHTML('afterbegin', previewRowMarkup);
    refreshScrollDrivenEffects();
}

function setPendingUploadPreviewError(message) {
    const previewRow = documentsTable?.querySelector('tr[data-pending-upload="true"]');
    if (!previewRow) return;

    previewRow.classList.add('upload-preview-row-error');
    const statusCell = previewRow.querySelector('[data-preview-status]');
    const actionsCell = previewRow.querySelector('[data-preview-actions]');

    if (statusCell) {
        statusCell.innerHTML = '<span class="badge badge-danger">Errore upload</span>';
    }
    if (actionsCell) {
        actionsCell.innerHTML = `<span class="upload-preview-note">${escapeHtml(message || 'Upload non riuscito')}</span>`;
    }
}

function setDashboardStatValue(target, value) {
    if (!target) return;
    const safeValue = Number.isFinite(value) ? Math.max(0, value) : 0;
    target.textContent = String(safeValue);
}

function isDashboardSectionVisible() {
    const currentId = activeSectionId || document.querySelector('.section.active')?.id || '';
    return currentId === DEFAULT_SECTION;
}

function getDashboardStatsSnapshot() {
    const assessmentsStats = getAssessmentsStatsFromList();
    return {
        documents: getDocumentsCountFromTable(),
        machines: Array.isArray(machinesCache) ? machinesCache.length : 0,
        assessments: assessmentsStats.total,
        conformi: assessmentsStats.conformi
    };
}

function getDashboardStatItems(snapshot) {
    return [
        { element: statDocumentsCount, value: snapshot.documents },
        { element: statMachinesCount, value: snapshot.machines },
        { element: statAssessmentsCount, value: snapshot.assessments },
        { element: statConformeCount, value: snapshot.conformi }
    ].map((item) => {
        if (!item.element) return null;
        return {
            ...item,
            value: Number.isFinite(item.value) ? Math.max(0, item.value) : 0,
            card: item.element.closest('.stat-card')
        };
    }).filter(Boolean);
}

function ensureStatCardProgressBar(card) {
    if (!card) return null;

    let progressEl = card.querySelector('.stat-progress');
    if (!progressEl) {
        progressEl = document.createElement('div');
        progressEl.className = 'stat-progress';
        progressEl.setAttribute('aria-hidden', 'true');
        progressEl.innerHTML = '<div class="stat-progress__bar"></div>';
        card.appendChild(progressEl);
    }
    return progressEl;
}

function updateDashboardProgressBars(statItems, phase = 1) {
    const safePhase = Number.isFinite(phase) ? Math.max(0, Math.min(1, phase)) : 1;
    const maxValue = statItems.reduce((max, item) => Math.max(max, item.value), 0);

    statItems.forEach((item) => {
        if (!item.card) return;
        ensureStatCardProgressBar(item.card);
        const ratio = maxValue > 0 ? item.value / maxValue : 0;
        item.card.style.setProperty('--stat-progress', (ratio * safePhase).toFixed(4));
    });
}

function cancelDashboardCountupAnimation() {
    if (dashboardCountupFrameId !== null) {
        cancelAnimationFrame(dashboardCountupFrameId);
        dashboardCountupFrameId = null;
    }
    dashboardCountupToken += 1;
}

function renderDashboardStatsImmediate(statItems) {
    statItems.forEach((item) => {
        setDashboardStatValue(item.element, item.value);
    });
    updateDashboardProgressBars(statItems, 1);
}

function animateDashboardStatsFromZero(statItems) {
    if (!statItems.length) return;

    const prefersReducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion) {
        renderDashboardStatsImmediate(statItems);
        return;
    }

    cancelDashboardCountupAnimation();
    dashboardCountupToken += 1;
    const token = dashboardCountupToken;
    const startTime = performance.now();

    statItems.forEach((item) => setDashboardStatValue(item.element, 0));
    updateDashboardProgressBars(statItems, 0);

    const tick = (now) => {
        if (token !== dashboardCountupToken) return;

        const elapsed = now - startTime;
        const progress = Math.max(0, Math.min(1, elapsed / DASHBOARD_COUNTUP_DURATION_MS));
        const eased = 1 - Math.pow(1 - progress, 3);

        statItems.forEach((item) => {
            const nextValue = Math.round(item.value * eased);
            setDashboardStatValue(item.element, nextValue);
        });
        updateDashboardProgressBars(statItems, eased);

        if (progress < 1) {
            dashboardCountupFrameId = requestAnimationFrame(tick);
            return;
        }

        dashboardCountupFrameId = null;
        renderDashboardStatsImmediate(statItems);
    };

    dashboardCountupFrameId = requestAnimationFrame(tick);
}

function normalizeRiskLevelLabel(level) {
    const normalizedLevel = String(level || '').trim().toLowerCase();
    if (normalizedLevel.startsWith('bass')) return 'Basso';
    if (normalizedLevel.startsWith('med')) return 'Medio';
    if (normalizedLevel.startsWith('alt')) return 'Alto';
    if (normalizedLevel.startsWith('crit')) return 'Critico';
    return '';
}

function getAssessmentTimestamp(assessment) {
    const dateCandidates = [
        assessment?.dataAssessment,
        assessment?.createdAt,
        assessment?.dataCreazione,
        assessment?.createdDate
    ];

    for (const candidate of dateCandidates) {
        const parsed = parseAssessmentDateCandidate(candidate);
        if (parsed && Number.isFinite(parsed.getTime())) {
            return parsed.getTime();
        }
    }

    const numericId = Number(assessment?.id);
    return Number.isFinite(numericId) ? numericId : 0;
}

function parseAssessmentDateCandidate(candidate) {
    if (!candidate) return null;

    if (candidate instanceof Date) {
        const dateClone = new Date(candidate.getTime());
        return Number.isFinite(dateClone.getTime()) ? dateClone : null;
    }

    if (typeof candidate === 'number') {
        const numericDate = new Date(candidate);
        return Number.isFinite(numericDate.getTime()) ? numericDate : null;
    }

    if (typeof candidate !== 'string') return null;
    const value = candidate.trim();
    if (!value) return null;

    const dateOnlyMatch = value.match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (dateOnlyMatch) {
        const [, year, month, day] = dateOnlyMatch;
        const localDate = new Date(Number(year), Number(month) - 1, Number(day));
        return Number.isFinite(localDate.getTime()) ? localDate : null;
    }

    const dateTimeWithoutZoneMatch = value.match(
        /^(\d{4})-(\d{2})-(\d{2})[T\s](\d{2}):(\d{2})(?::(\d{2})(?:\.(\d{1,6}))?)?$/
    );
    if (dateTimeWithoutZoneMatch) {
        const [, year, month, day, hour, minute, second, fraction] = dateTimeWithoutZoneMatch;
        const milliseconds = fraction ? Number(String(fraction).slice(0, 3).padEnd(3, '0')) : 0;
        const localDate = new Date(
            Number(year),
            Number(month) - 1,
            Number(day),
            Number(hour),
            Number(minute),
            Number(second || 0),
            milliseconds
        );
        return Number.isFinite(localDate.getTime()) ? localDate : null;
    }

    const parsed = new Date(value);
    return Number.isFinite(parsed.getTime()) ? parsed : null;
}

function getAssessmentDate(assessment) {
    const dateCandidates = [
        assessment?.dataAssessment,
        assessment?.createdAt,
        assessment?.dataCreazione,
        assessment?.createdDate
    ];

    for (const candidate of dateCandidates) {
        const parsed = parseAssessmentDateCandidate(candidate);
        if (parsed) return parsed;
    }
    return null;
}

function toLocalDateKey(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function getWeekStart(dateInput) {
    const date = new Date(dateInput.getTime());
    date.setHours(0, 0, 0, 0);
    const day = date.getDay();
    const mondayOffset = (day + 6) % 7;
    date.setDate(date.getDate() - mondayOffset);
    return date;
}

function formatVelocityShortWeekLabel(date) {
    return date.toLocaleDateString('it-IT', {
        day: '2-digit',
        month: 'short'
    });
}

function formatVelocityFullDateLabel(date) {
    return date.toLocaleDateString('it-IT', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
    });
}

function getAssessmentVelocityAnchorWeek(assessments) {
    const source = Array.isArray(assessments) ? assessments : [];
    let latestDate = null;

    source.forEach((assessment) => {
        const assessmentDate = getAssessmentDate(assessment);
        if (!assessmentDate) return;
        if (!latestDate || assessmentDate > latestDate) {
            latestDate = assessmentDate;
        }
    });

    return getWeekStart(latestDate || new Date());
}

function buildAssessmentsVelocityWeeks(assessments, weeksCount = ASSESSMENT_VELOCITY_WEEKS) {
    const safeWeeksCount = Math.max(1, Number(weeksCount) || ASSESSMENT_VELOCITY_WEEKS);
    const anchorWeekStart = getAssessmentVelocityAnchorWeek(assessments);
    const lastWeekStart = new Date(anchorWeekStart.getTime());

    const firstWeekStart = new Date(lastWeekStart.getTime());
    firstWeekStart.setDate(firstWeekStart.getDate() - ((safeWeeksCount - 1) * 7));

    const buckets = Array.from({ length: safeWeeksCount }, (_, index) => {
        const start = new Date(firstWeekStart.getTime());
        start.setDate(firstWeekStart.getDate() + (index * 7));
        start.setHours(0, 0, 0, 0);

        const end = new Date(start.getTime());
        end.setDate(start.getDate() + 6);
        end.setHours(23, 59, 59, 999);

        return {
            start,
            end,
            key: toLocalDateKey(start),
            total: 0,
            conformi: 0
        };
    });

    const bucketIndexByKey = new Map(buckets.map((bucket, index) => [bucket.key, index]));
    const source = Array.isArray(assessments) ? assessments : [];
    source.forEach((assessment) => {
        const assessmentDate = getAssessmentDate(assessment);
        if (!assessmentDate) return;

        const weekStart = getWeekStart(assessmentDate);
        const bucketIndex = bucketIndexByKey.get(toLocalDateKey(weekStart));
        if (bucketIndex === undefined) return;

        const bucket = buckets[bucketIndex];
        bucket.total += 1;
        if (resolveAssessmentStatus(assessment) === 'CONFORME') {
            bucket.conformi += 1;
        }
    });

    return {
        buckets,
        firstWeekStart,
        lastWeekStart,
        windowTotal: buckets.reduce((sum, bucket) => sum + bucket.total, 0)
    };
}

function buildLinePath(points, fieldName) {
    return points.map((point, index) => {
        const command = index === 0 ? 'M' : 'L';
        return `${command} ${point.x.toFixed(2)} ${point[fieldName].toFixed(2)}`;
    }).join(' ');
}

function renderAssessmentsVelocityChart() {
    if (!assessmentsVelocityChart) return;

    ensureAssessmentVelocityControls();

    const sourceAssessments = Array.isArray(assessmentsCache) ? assessmentsCache : [];
    const velocityWindow = buildAssessmentsVelocityWeeks(
        sourceAssessments,
        ASSESSMENT_VELOCITY_WEEKS
    );
    const buckets = velocityWindow.buckets;
    updateAssessmentVelocityControls(velocityWindow);

    if (!sourceAssessments.length) {
        assessmentsVelocityChart.innerHTML = `
            <div class="assessments-velocity-empty">
                Nessun assessment disponibile per costruire lo storico.
            </div>
        `;
        return;
    }

    const chartWidth = 760;
    const chartHeight = 286;
    const padding = { top: 22, right: 22, bottom: 52, left: 54 };
    const plotWidth = chartWidth - padding.left - padding.right;
    const plotHeight = chartHeight - padding.top - padding.bottom;
    const lineCount = Math.max(1, buckets.length - 1);
    const maxObserved = Math.max(0, ...buckets.map((bucket) => bucket.total), ...buckets.map((bucket) => bucket.conformi));
    const yStep = Math.max(1, Math.ceil(maxObserved / 4));
    const yMax = yStep * 4;
    const yTicks = [0, yStep, yStep * 2, yStep * 3, yMax];
    const toY = (value) => padding.top + plotHeight - ((value / yMax) * plotHeight);

    const points = buckets.map((bucket, index) => {
        const x = padding.left + ((index / lineCount) * plotWidth);
        return {
            bucket,
            x,
            yTotal: toY(bucket.total),
            yConformi: toY(bucket.conformi)
        };
    });

    const gridLinesMarkup = yTicks.slice().reverse().map((tickValue) => {
        const y = toY(tickValue);
        return `
            <line x1="${padding.left}" y1="${y.toFixed(2)}" x2="${(padding.left + plotWidth).toFixed(2)}" y2="${y.toFixed(2)}" class="assessments-velocity-grid-line"></line>
            <text x="${(padding.left - 10).toFixed(2)}" y="${(y + 4).toFixed(2)}" text-anchor="end" class="assessments-velocity-axis-label">${tickValue}</text>
        `;
    }).join('');

    const xLabelsMarkup = points.map((point) => `
        <text x="${point.x.toFixed(2)}" y="${(padding.top + plotHeight + 22).toFixed(2)}" text-anchor="middle" class="assessments-velocity-x-label">
            ${formatVelocityShortWeekLabel(point.bucket.start)}
        </text>
    `).join('');

    const pointsMarkup = points.map((point) => {
        const tooltip = `Settimana ${formatVelocityFullDateLabel(point.bucket.start)} - ${formatVelocityFullDateLabel(point.bucket.end)} | Totale: ${point.bucket.total} | CONFORME: ${point.bucket.conformi}`;
        return `
            <g class="assessments-velocity-point-group">
                <title>${escapeHtml(tooltip)}</title>
                <circle cx="${point.x.toFixed(2)}" cy="${point.yTotal.toFixed(2)}" r="4.2" class="assessments-velocity-point assessments-velocity-point--total"></circle>
                <circle cx="${point.x.toFixed(2)}" cy="${point.yConformi.toFixed(2)}" r="4.2" class="assessments-velocity-point assessments-velocity-point--conforme"></circle>
            </g>
        `;
    }).join('');

    const xAxisY = padding.top + plotHeight;
    const yAxisX = padding.left;
    const firstBucket = buckets[0];
    const lastBucket = buckets[buckets.length - 1];
    const chartAriaLabel = `Andamento assessment dal ${formatVelocityFullDateLabel(firstBucket.start)} al ${formatVelocityFullDateLabel(lastBucket.end)} nelle ultime ${ASSESSMENT_VELOCITY_WEEKS} settimane.`;
    const plotEnd = padding.left + plotWidth;

    assessmentsVelocityChart.innerHTML = `
        <svg class="assessments-velocity-chart-svg"
             viewBox="0 0 ${chartWidth} ${chartHeight}"
             role="img"
             aria-label="${escapeHtml(chartAriaLabel)}">
            ${gridLinesMarkup}
            <line x1="${yAxisX}" y1="${padding.top}" x2="${yAxisX}" y2="${xAxisY}" class="assessments-velocity-axis-line"></line>
            <line x1="${padding.left}" y1="${xAxisY}" x2="${plotEnd.toFixed(2)}" y2="${xAxisY}" class="assessments-velocity-axis-line"></line>
            <path d="${buildLinePath(points, 'yTotal')}" class="assessments-velocity-line assessments-velocity-line--total" style="stroke:${ASSESSMENT_VELOCITY_TOTAL_COLOR};"></path>
            <path d="${buildLinePath(points, 'yConformi')}" class="assessments-velocity-line assessments-velocity-line--conforme" style="stroke:${ASSESSMENT_VELOCITY_CONFORME_COLOR};"></path>
            ${xLabelsMarkup}
            ${pointsMarkup}
        </svg>
        <div class="assessments-velocity-legend" aria-hidden="true">
            <span class="assessments-velocity-legend-item">
                <span class="assessments-velocity-legend-dot assessments-velocity-legend-dot--total"></span>
                Totale assessment
            </span>
            <span class="assessments-velocity-legend-item">
                <span class="assessments-velocity-legend-dot assessments-velocity-legend-dot--conforme"></span>
                Assessment CONFORME
            </span>
        </div>
    `;
}

function interpolateRgbChannel(start, end, ratio) {
    return Math.round(start + ((end - start) * ratio));
}

function buildAssessmentHeatmapColor(successRatio) {
    const ratio = Math.max(0, Math.min(1, Number(successRatio) || 0));
    const red = [185, 28, 28];
    const yellow = [180, 83, 9];
    const green = [22, 128, 60];

    const start = ratio <= 0.5 ? red : yellow;
    const end = ratio <= 0.5 ? yellow : green;
    const localRatio = ratio <= 0.5 ? (ratio / 0.5) : ((ratio - 0.5) / 0.5);

    const r = interpolateRgbChannel(start[0], end[0], localRatio);
    const g = interpolateRgbChannel(start[1], end[1], localRatio);
    const b = interpolateRgbChannel(start[2], end[2], localRatio);
    return `rgb(${r}, ${g}, ${b})`;
}

function buildAssessmentHeatmapInitials(machineName) {
    const raw = String(machineName || '').trim();
    if (!raw) return '?';
    const parts = raw.split(/\s+/).filter(Boolean);
    if (!parts.length) return '?';
    if (parts.length === 1) {
        return parts[0].slice(0, 2).toUpperCase();
    }
    return `${parts[0][0] || ''}${parts[1][0] || ''}`.toUpperCase();
}

function isAssessmentOkForHeatmap(assessment) {
    const status = resolveAssessmentStatus(assessment);
    if (status === 'CONFORME') return true;
    if (status === 'NON_CONFORME') return false;

    const score = normalizeAssessmentScore(assessment?.punteggioConformita);
    if (score !== null) {
        return score >= 70;
    }

    return normalizeRiskLevelLabel(assessment?.livelloRischio) === 'Basso';
}

function buildAssessmentHeatmapMachineLookup(assessments) {
    const byId = new Map();
    const byName = new Map();
    const source = Array.isArray(assessments) ? assessments : [];

    const add = (map, key, assessment) => {
        if (key === null || key === undefined || key === '') return;
        const current = map.get(key);
        if (current) {
            current.push(assessment);
            return;
        }
        map.set(key, [assessment]);
    };

    source.forEach((assessment) => {
        const machineId = getAssessmentMachineId(assessment);
        const machineNameKey = normalizeMachineNameKey(
            assessment?.macchinarioNome
            || assessment?.machineName
            || assessment?.nomeMacchinario
        );

        if (Number.isFinite(machineId)) {
            add(byId, machineId, assessment);
        }
        if (machineNameKey) {
            add(byName, machineNameKey, assessment);
        }
    });

    const sortByRecent = (entriesMap) => {
        entriesMap.forEach((list) => {
            list.sort((a, b) => getAssessmentTimestamp(b) - getAssessmentTimestamp(a));
        });
    };

    sortByRecent(byId);
    sortByRecent(byName);
    return { byId, byName };
}

function getRecentAssessmentsWindow(assessments, lookbackWindow) {
    const source = Array.isArray(assessments) ? assessments.slice() : [];
    const safeWindow = Math.max(ASSESSMENT_HEATMAP_MIN_WINDOW, Number(lookbackWindow) || ASSESSMENT_HEATMAP_DEFAULT_WINDOW);
    return source
        .sort((a, b) => getAssessmentTimestamp(b) - getAssessmentTimestamp(a))
        .slice(0, safeWindow);
}

function buildAssessmentHeatmapRows(machines, assessments, lookbackWindow) {
    const sourceMachines = Array.isArray(machines) ? machines : [];
    if (!sourceMachines.length) return [];

    const safeWindow = Math.max(ASSESSMENT_HEATMAP_MIN_WINDOW, Number(lookbackWindow) || ASSESSMENT_HEATMAP_DEFAULT_WINDOW);
    const lookup = buildAssessmentHeatmapMachineLookup(assessments);

    return sourceMachines.map((machine, index) => {
        const machineId = Number(machine?.id);
        const machineName = String(machine?.nome || `Macchinario ${index + 1}`).trim() || `Macchinario ${index + 1}`;

        let relatedAssessments = [];
        if (Number.isFinite(machineId)) {
            relatedAssessments = lookup.byId.get(machineId) || [];
        }

        if (!relatedAssessments.length) {
            const machineNameKey = normalizeMachineNameKey(machineName);
            if (machineNameKey) {
                relatedAssessments = lookup.byName.get(machineNameKey) || [];
            }
        }

        const recentAssessments = relatedAssessments.slice(0, safeWindow);
        const totalAssessments = recentAssessments.length;
        const okAssessments = recentAssessments.filter((assessment) => isAssessmentOkForHeatmap(assessment)).length;
        const successRatio = totalAssessments > 0 ? (okAssessments / totalAssessments) : 0;
        const lastAssessmentDate = totalAssessments > 0 ? getAssessmentDate(recentAssessments[0]) : null;

        return {
            machineId: Number.isFinite(machineId) ? machineId : null,
            machineName,
            okAssessments,
            totalAssessments,
            successRatio,
            lastAssessmentDate
        };
    }).sort((a, b) => a.machineName.localeCompare(b.machineName, 'it-IT', { sensitivity: 'base' }));
}

function drawAssessmentHeatmapRoundedRect(ctx, x, y, width, height, radius) {
    const safeRadius = Math.max(0, Math.min(radius, width / 2, height / 2));
    ctx.beginPath();
    ctx.moveTo(x + safeRadius, y);
    ctx.lineTo(x + width - safeRadius, y);
    ctx.quadraticCurveTo(x + width, y, x + width, y + safeRadius);
    ctx.lineTo(x + width, y + height - safeRadius);
    ctx.quadraticCurveTo(x + width, y + height, x + width - safeRadius, y + height);
    ctx.lineTo(x + safeRadius, y + height);
    ctx.quadraticCurveTo(x, y + height, x, y + height - safeRadius);
    ctx.lineTo(x, y + safeRadius);
    ctx.quadraticCurveTo(x, y, x + safeRadius, y);
    ctx.closePath();
}

function renderAssessmentHeatmapLegend(rows, lookbackWindow) {
    if (!assessmentHeatmapLegend) return;

    const totalMachines = Array.isArray(rows) ? rows.length : 0;
    const machinesWithData = (rows || []).filter((row) => row.totalAssessments > 0).length;
    const machinesWithoutData = Math.max(0, totalMachines - machinesWithData);

    assessmentHeatmapLegend.innerHTML = `
        <div class="assessment-heatmap-legend__scale">
            <span class="assessment-heatmap-legend__value">Meno conforme</span>
            <span class="assessment-heatmap-legend__track"></span>
            <span class="assessment-heatmap-legend__value">Più conforme</span>
        </div>
        <div class="assessment-heatmap-legend__scale-notes" aria-hidden="true">
            <span>0%</span>
            <span>50%</span>
            <span>100%</span>
        </div>
        <div class="assessment-heatmap-legend__stats">
            <span class="assessment-heatmap-legend__chip">Con dati: ${machinesWithData}/${totalMachines}</span>
            <span class="assessment-heatmap-legend__chip">Senza recenti: ${machinesWithoutData}</span>
            <span class="assessment-heatmap-legend__chip">Riferimento: ultimi ${Math.max(ASSESSMENT_HEATMAP_MIN_WINDOW, Number(lookbackWindow) || ASSESSMENT_HEATMAP_DEFAULT_WINDOW)} assessment</span>
        </div>
    `;
}

function showAssessmentHeatmapTooltip(machineName, localX, localY, containerRect) {
    if (!assessmentHeatmapTooltip) return;

    assessmentHeatmapTooltip.textContent = String(machineName || 'Macchinario');
    assessmentHeatmapTooltip.style.display = 'block';
    assessmentHeatmapTooltip.style.visibility = 'hidden';

    const tooltipWidth = assessmentHeatmapTooltip.offsetWidth || 0;
    const tooltipHeight = assessmentHeatmapTooltip.offsetHeight || 0;
    const offset = 12;
    const maxLeft = Math.max(8, containerRect.width - tooltipWidth - 8);
    const maxTop = Math.max(8, containerRect.height - tooltipHeight - 8);
    const left = Math.max(8, Math.min(maxLeft, localX + offset));
    const top = Math.max(8, Math.min(maxTop, localY + offset));

    assessmentHeatmapTooltip.style.left = `${left}px`;
    assessmentHeatmapTooltip.style.top = `${top}px`;
    assessmentHeatmapTooltip.style.visibility = 'visible';
    assessmentHeatmapTooltip.setAttribute('aria-hidden', 'false');
}

function renderAssessmentsMachineHeatmap() {
    if (!assessmentHeatmapCanvas) return;
    const ctx = assessmentHeatmapCanvas.getContext('2d');
    if (!ctx) return;

    const lookbackWindow = getAssessmentHeatmapWindowSize();
    const recentAssessments = getRecentAssessmentsWindow(assessmentsCache, lookbackWindow);
    const rows = buildAssessmentHeatmapRows(machinesCache, recentAssessments, lookbackWindow);

    if (assessmentHeatmapWindowSelect && assessmentHeatmapWindowSelect.value !== String(lookbackWindow)) {
        assessmentHeatmapWindowSelect.value = String(lookbackWindow);
    }

    if (assessmentHeatmapEmpty) {
        assessmentHeatmapEmpty.style.display = rows.length ? 'none' : 'block';
    }

    hideAssessmentHeatmapTooltip();
    assessmentHeatmapCells = [];
    renderAssessmentHeatmapLegend(rows, lookbackWindow);

    const fallbackWidth = Math.max(280, Math.floor(assessmentHeatmapCanvas.parentElement?.clientWidth || 520));
    const fallbackHeight = 180;

    if (!rows.length) {
        assessmentHeatmapCanvas.width = fallbackWidth;
        assessmentHeatmapCanvas.height = fallbackHeight;
        assessmentHeatmapCanvas.style.width = '100%';
        assessmentHeatmapCanvas.style.height = `${fallbackHeight}px`;
        assessmentHeatmapCanvas.setAttribute('aria-label', 'Heatmap assessment non disponibile: nessun macchinario registrato');

        ctx.clearRect(0, 0, fallbackWidth, fallbackHeight);
        ctx.fillStyle = 'rgba(100, 116, 139, 0.85)';
        ctx.font = '14px system-ui, -apple-system, "Segoe UI", sans-serif';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText('Nessun macchinario disponibile', fallbackWidth / 2, fallbackHeight / 2);
        return;
    }

    const horizontalPadding = 14;
    const verticalPadding = 14;
    const minCellWithGap = ASSESSMENT_HEATMAP_MIN_CELL_SIZE + ASSESSMENT_HEATMAP_CELL_GAP;
    const maxColumnsByWidth = Math.max(
        1,
        Math.floor((fallbackWidth - (horizontalPadding * 2) + ASSESSMENT_HEATMAP_CELL_GAP) / minCellWithGap)
    );
    const columns = Math.max(1, Math.min(rows.length, maxColumnsByWidth));
    const rowCount = Math.ceil(rows.length / columns);

    const innerWidth = fallbackWidth - (horizontalPadding * 2);
    const rawCellSize = Math.floor((innerWidth - ((columns - 1) * ASSESSMENT_HEATMAP_CELL_GAP)) / columns);
    const cellSize = Math.max(ASSESSMENT_HEATMAP_MIN_CELL_SIZE, Math.min(ASSESSMENT_HEATMAP_MAX_CELL_SIZE, rawCellSize));
    const gridWidth = (columns * cellSize) + ((columns - 1) * ASSESSMENT_HEATMAP_CELL_GAP);
    const startX = Math.round((fallbackWidth - gridWidth) / 2);
    const canvasHeight = (verticalPadding * 2) + (rowCount * cellSize) + ((rowCount - 1) * ASSESSMENT_HEATMAP_CELL_GAP);

    const dpr = window.devicePixelRatio || 1;
    assessmentHeatmapCanvas.width = Math.max(1, Math.round(fallbackWidth * dpr));
    assessmentHeatmapCanvas.height = Math.max(1, Math.round(canvasHeight * dpr));
    assessmentHeatmapCanvas.style.width = `${fallbackWidth}px`;
    assessmentHeatmapCanvas.style.height = `${canvasHeight}px`;
    assessmentHeatmapCanvas.setAttribute(
        'aria-label',
        `Heatmap salute di ${rows.length} macchinari sugli ultimi ${lookbackWindow} assessment`
    );

    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    ctx.clearRect(0, 0, fallbackWidth, canvasHeight);

    rows.forEach((row, index) => {
        const col = index % columns;
        const currentRow = Math.floor(index / columns);
        const x = startX + (col * (cellSize + ASSESSMENT_HEATMAP_CELL_GAP));
        const y = verticalPadding + (currentRow * (cellSize + ASSESSMENT_HEATMAP_CELL_GAP));
        const hasData = row.totalAssessments > 0;

        ctx.fillStyle = hasData ? buildAssessmentHeatmapColor(row.successRatio) : '#cbd5e1';
        drawAssessmentHeatmapRoundedRect(ctx, x, y, cellSize, cellSize, Math.max(4, Math.floor(cellSize * 0.22)));
        ctx.fill();

        ctx.strokeStyle = hasData ? 'rgba(15, 23, 42, 0.16)' : 'rgba(100, 116, 139, 0.24)';
        ctx.lineWidth = 1;
        ctx.stroke();

        const initials = buildAssessmentHeatmapInitials(row.machineName);
        const useDarkText = !hasData || (row.successRatio >= 0.35 && row.successRatio <= 0.72);
        ctx.fillStyle = useDarkText ? 'rgba(15, 23, 42, 0.9)' : '#ffffff';
        ctx.font = `${Math.max(10, Math.min(14, Math.round(cellSize * 0.34)))}px system-ui, -apple-system, "Segoe UI", sans-serif`;
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(initials, x + (cellSize / 2), y + (cellSize / 2));

        assessmentHeatmapCells.push({
            x,
            y,
            size: cellSize,
            row
        });
    });
}

function handleAssessmentHeatmapPointerMove(event) {
    if (!assessmentHeatmapCanvas || !assessmentHeatmapCells.length) {
        hideAssessmentHeatmapTooltip();
        return;
    }

    const rect = assessmentHeatmapCanvas.getBoundingClientRect();
    const localX = event.clientX - rect.left;
    const localY = event.clientY - rect.top;
    const hit = assessmentHeatmapCells.find((cell) => {
        return localX >= cell.x
            && localX <= (cell.x + cell.size)
            && localY >= cell.y
            && localY <= (cell.y + cell.size);
    });

    if (!hit) {
        hideAssessmentHeatmapTooltip();
        return;
    }

    assessmentHeatmapCanvas.style.cursor = 'pointer';
    const containerRect = assessmentHeatmapCanvas.parentElement?.getBoundingClientRect() || rect;
    showAssessmentHeatmapTooltip(hit.row.machineName, localX, localY, containerRect);
}

function clamp01(value) {
    if (!Number.isFinite(value)) return 0;
    return Math.max(0, Math.min(1, value));
}

function computeAssessmentIntervalsInDays(assessments) {
    const timestamps = (Array.isArray(assessments) ? assessments : [])
        .map((assessment) => getAssessmentDate(assessment))
        .filter((date) => date instanceof Date && Number.isFinite(date.getTime()))
        .map((date) => date.getTime())
        .sort((a, b) => b - a);

    if (timestamps.length < 2) return [];

    const dayMs = 24 * 60 * 60 * 1000;
    const intervals = [];
    for (let index = 0; index < timestamps.length - 1; index += 1) {
        const delta = (timestamps[index] - timestamps[index + 1]) / dayMs;
        if (Number.isFinite(delta) && delta >= 0) {
            intervals.push(delta);
        }
    }

    return intervals;
}

function computeAssessmentCadenceMetrics(assessments) {
    const intervals = computeAssessmentIntervalsInDays(assessments);
    if (!intervals.length) {
        return {
            intervalCount: 0,
            averageDays: null,
            stdDevDays: null,
            coefficientVariation: null
        };
    }

    const total = intervals.reduce((sum, value) => sum + value, 0);
    const averageDays = total / intervals.length;
    const variance = intervals.reduce((sum, value) => {
        const diff = value - averageDays;
        return sum + (diff * diff);
    }, 0) / intervals.length;
    const stdDevDays = Math.sqrt(Math.max(0, variance));
    const coefficientVariation = averageDays > 0 ? (stdDevDays / averageDays) : 0;

    return {
        intervalCount: intervals.length,
        averageDays,
        stdDevDays,
        coefficientVariation
    };
}

function computeAssessmentCadenceHealthScore(averageDays, coefficientVariation) {
    const safeAverageDays = Math.max(0, Number(averageDays) || 0);
    const threshold = ASSESSMENT_CADENCE_TARGET_DAYS;
    const maxTolerated = threshold * ASSESSMENT_CADENCE_TOLERANCE_FACTOR;

    let baselineScore;
    if (safeAverageDays <= threshold) {
        baselineScore = 1;
    } else if (safeAverageDays >= maxTolerated) {
        baselineScore = 0;
    } else {
        baselineScore = 1 - ((safeAverageDays - threshold) / (maxTolerated - threshold));
    }

    const variabilityPenalty = Math.min(0.35, Math.max(0, Number(coefficientVariation) || 0) * 0.35);
    return clamp01(baselineScore - variabilityPenalty);
}

function buildAssessmentCadenceRows(machines, assessments, lookbackWindow) {
    const sourceMachines = Array.isArray(machines) ? machines : [];
    if (!sourceMachines.length) return [];

    const safeWindow = Math.max(ASSESSMENT_HEATMAP_MIN_WINDOW, Number(lookbackWindow) || ASSESSMENT_HEATMAP_DEFAULT_WINDOW);
    const lookup = buildAssessmentHeatmapMachineLookup(assessments);

    return sourceMachines.map((machine, index) => {
        const machineId = Number(machine?.id);
        const machineName = String(machine?.nome || `Macchinario ${index + 1}`).trim() || `Macchinario ${index + 1}`;

        let relatedAssessments = [];
        if (Number.isFinite(machineId)) {
            relatedAssessments = lookup.byId.get(machineId) || [];
        }
        if (!relatedAssessments.length) {
            const machineNameKey = normalizeMachineNameKey(machineName);
            if (machineNameKey) {
                relatedAssessments = lookup.byName.get(machineNameKey) || [];
            }
        }

        const recentAssessments = relatedAssessments.slice(0, safeWindow);
        const cadenceMetrics = computeAssessmentCadenceMetrics(recentAssessments);
        const hasEnoughData = cadenceMetrics.intervalCount > 0;
        const averageDays = hasEnoughData ? cadenceMetrics.averageDays : null;
        const stdDevDays = hasEnoughData ? cadenceMetrics.stdDevDays : null;

        const fallbackDays = ASSESSMENT_CADENCE_TARGET_DAYS * ASSESSMENT_CADENCE_TOLERANCE_FACTOR;
        const plotDays = hasEnoughData ? Math.max(0, Number(averageDays) || 0) : fallbackDays;
        const healthScore = hasEnoughData
            ? computeAssessmentCadenceHealthScore(averageDays, cadenceMetrics.coefficientVariation)
            : 0;

        return {
            machineId: Number.isFinite(machineId) ? machineId : null,
            machineName,
            initials: buildAssessmentHeatmapInitials(machineName),
            averageDays,
            stdDevDays,
            hasEnoughData,
            plotDays,
            totalAssessments: recentAssessments.length,
            intervalCount: cadenceMetrics.intervalCount,
            healthScore
        };
    }).sort((a, b) => a.machineName.localeCompare(b.machineName, 'it-IT', { sensitivity: 'base' }));
}

function formatAssessmentCadenceDaysLabel(averageDays, hasEnoughData) {
    if (!hasEnoughData || !Number.isFinite(averageDays)) return 'N/D';
    const rounded = Math.round(averageDays * 10) / 10;
    return `${rounded} g`;
}

function computeCadenceAxisMaxValue(rows) {
    const target = ASSESSMENT_CADENCE_TARGET_DAYS;
    const observedMax = Math.max(
        target * ASSESSMENT_CADENCE_TOLERANCE_FACTOR,
        ...rows.map((row) => Math.max(0, Number(row?.plotDays) || 0))
    );

    const step = observedMax > 160 ? 20 : observedMax > 80 ? 10 : 5;
    return Math.max(step, Math.ceil(observedMax / step) * step);
}

function getAssessmentCadenceStateClass(row) {
    if (!row?.hasEnoughData) return 'unknown';
    const score = Number(row?.healthScore);
    if (!Number.isFinite(score)) return 'unknown';
    if (score >= 0.72) return 'high';
    if (score >= 0.42) return 'medium';
    return 'low';
}

function renderAssessmentsMaintenanceCadenceChart() {
    if (!assessmentCadenceChart) return;

    const lookbackWindow = getAssessmentHeatmapWindowSize();
    const rows = buildAssessmentCadenceRows(machinesCache, assessmentsCache, lookbackWindow);
    const machinesWithoutIntervals = rows.filter((row) => !row.hasEnoughData).length;
    const hasAtLeastOneComputedRow = rows.some((row) => row.hasEnoughData);

    if (assessmentCadenceTargetLabel) {
        assessmentCadenceTargetLabel.textContent = `Target: ${ASSESSMENT_CADENCE_TARGET_DAYS} giorni`;
    }

    if (assessmentCadenceEmpty) {
        if (!rows.length) {
            assessmentCadenceEmpty.textContent = 'Nessun macchinario disponibile.';
            assessmentCadenceEmpty.style.display = 'block';
        } else if (!hasAtLeastOneComputedRow) {
            assessmentCadenceEmpty.textContent = 'Dati insufficienti per stimare intervalli reali: servono almeno 2 assessment per macchinario.';
            assessmentCadenceEmpty.style.display = 'block';
        } else {
            assessmentCadenceEmpty.style.display = 'none';
        }
    }

    if (!rows.length) {
        assessmentCadenceChart.innerHTML = '';
        return;
    }

    const axisMax = computeCadenceAxisMaxValue(rows);
    const targetRatio = axisMax > 0
        ? Math.max(0, Math.min(1, ASSESSMENT_CADENCE_TARGET_DAYS / axisMax))
        : 0;
    const gridTicks = Array.from({ length: 5 }, (_, index) => Math.round((axisMax * index) / 4));

    const summaryMarkup = `
        <div class="assessment-cadence-chart__summary" aria-hidden="true">
            <span class="assessment-cadence-chart__summary-title">Intervallo medio rilevato</span>
            <span class="assessment-cadence-chart__summary-target">Target ${ASSESSMENT_CADENCE_TARGET_DAYS}g</span>
        </div>
        <div class="assessment-cadence-chart__scale" aria-hidden="true">
            ${gridTicks.map((tick, index) => `
                <span class="assessment-cadence-chart__tick${index === 0 ? ' is-start' : ''}${index === gridTicks.length - 1 ? ' is-end' : ''}">
                    ${tick}g
                </span>
            `).join('')}
        </div>
    `;

    const rowsMarkup = rows.map((row) => {
        const safeMachineName = escapeHtml(row.machineName);
        const ratio = row.hasEnoughData && axisMax > 0
            ? Math.max(0, Math.min(1, (Number(row.plotDays) || 0) / axisMax))
            : 0.16;
        const fill = row.hasEnoughData ? buildAssessmentHeatmapColor(row.healthScore) : '#94a3b8';
        const stateClass = getAssessmentCadenceStateClass(row);
        const averageLabel = formatAssessmentCadenceDaysLabel(row.averageDays, row.hasEnoughData);
        const varianceValue = row.hasEnoughData && Number.isFinite(row.stdDevDays)
            ? `Var. ${Math.round(row.stdDevDays * 10) / 10}g`
            : 'Dati insufficienti';
        const contextLabel = row.hasEnoughData
            ? `${row.totalAssessments} assessment recenti · ${row.intervalCount} intervalli`
            : `Meno di 2 assessment negli ultimi ${lookbackWindow}`;
        const tooltipText = row.hasEnoughData
            ? `${row.machineName}: media ${Math.round((row.averageDays || 0) * 10) / 10} giorni, ${varianceValue.toLowerCase()}`
            : `${row.machineName}: dati insufficienti per stimare la cadenza`;

        return `
            <article class="assessment-cadence-row assessment-cadence-row--${stateClass}${row.hasEnoughData ? '' : ' is-muted'}"
                     style="--assessment-cadence-ratio:${(ratio * 100).toFixed(2)}%; --assessment-cadence-target:${(targetRatio * 100).toFixed(2)}%; --assessment-cadence-fill:${fill};"
                     title="${escapeHtml(tooltipText)}">
                <div class="assessment-cadence-row__meta">
                    <div class="assessment-cadence-row__heading">
                        <strong class="assessment-cadence-row__name" title="${safeMachineName}">${safeMachineName}</strong>
                        <span class="assessment-cadence-row__context">${escapeHtml(contextLabel)}</span>
                    </div>
                    <span class="assessment-cadence-row__value assessment-cadence-row__value--${stateClass}">
                        ${escapeHtml(averageLabel)}
                    </span>
                </div>
                <div class="assessment-cadence-row__bar">
                    <div class="assessment-cadence-row__track" aria-hidden="true">
                        <span class="assessment-cadence-row__target"></span>
                        <span class="assessment-cadence-row__fill"></span>
                    </div>
                    <span class="assessment-cadence-row__variance">${escapeHtml(varianceValue)}</span>
                </div>
            </article>
        `;
    }).join('');

    const noteMarkup = machinesWithoutIntervals > 0
        ? `<div class="assessment-cadence-note">Macchinari con dati insufficienti (meno di 2 assessment negli ultimi ${lookbackWindow}): ${machinesWithoutIntervals}</div>`
        : '';

    assessmentCadenceChart.innerHTML = `
        <div class="assessment-cadence-chart" role="img"
             aria-label="Tempo medio tra assessment per macchinario in giorni, calcolato sugli ultimi ${lookbackWindow} assessment. Target ${ASSESSMENT_CADENCE_TARGET_DAYS} giorni.">
            ${summaryMarkup}
            <div class="assessment-cadence-chart__rows">
                ${rowsMarkup}
            </div>
        </div>
        ${noteMarkup}
    `;
}

function renderAssessmentInsightsFromWindow() {
    renderAssessmentsMachineHeatmap();
    renderAssessmentsMaintenanceCadenceChart();
}

function renderDashboardRiskChart() {
    if (!dashboardRiskChart) return;

    const riskRows = [
        { label: 'Basso', modifier: 'basso' },
        { label: 'Medio', modifier: 'medio' },
        { label: 'Alto', modifier: 'alto' },
        { label: 'Critico', modifier: 'critico' }
    ].map((row) => {
        const count = assessmentsCache.filter((assessment) => normalizeRiskLevelLabel(assessment?.livelloRischio) === row.label).length;
        return { ...row, count };
    });

    if (!assessmentsCache.length) {
        dashboardRiskChart.innerHTML = `
            <div class="dashboard-chart-empty">
                Nessun assessment disponibile per costruire il grafico rischio.
            </div>
        `;
        return;
    }

    const maxCount = Math.max(1, ...riskRows.map((row) => row.count));
    const totalCount = Math.max(1, assessmentsCache.length);

    dashboardRiskChart.innerHTML = `
        <div class="dashboard-risk-meters" role="list" aria-label="Distribuzione assessment per livello di rischio">
            ${riskRows.map((row) => {
                const share = Math.round((row.count / totalCount) * 100);
                const widthPercent = row.count > 0 ? Math.max(12, Math.round((row.count / maxCount) * 100)) : 0;
                return `
                    <div class="dashboard-risk-meter dashboard-risk-meter--${row.modifier}" role="listitem" style="--dashboard-risk-fill: ${widthPercent}%;">
                        <div class="dashboard-risk-meter__label-wrap">
                            <span class="dashboard-risk-meter__marker" aria-hidden="true"></span>
                            <span class="dashboard-risk-meter__label">${row.label}</span>
                        </div>
                        <div class="dashboard-risk-meter__track" aria-hidden="true">
                            <span class="dashboard-risk-meter__fill"></span>
                        </div>
                        <div class="dashboard-risk-meter__stats">
                            <strong>${row.count}</strong>
                            <span>${share}%</span>
                        </div>
                    </div>
                `;
            }).join('')}
        </div>
    `;
}

function renderDashboardRecentActivity() {
    if (!dashboardRecentList) return;

    if (!assessmentsCache.length) {
        dashboardRecentList.innerHTML = `
            <li class="dashboard-activity-empty">Nessuna attività recente disponibile.</li>
        `;
        return;
    }

    const recentAssessments = [...assessmentsCache]
        .sort((a, b) => getAssessmentTimestamp(b) - getAssessmentTimestamp(a))
        .slice(0, 5);

    dashboardRecentList.innerHTML = recentAssessments.map((assessment) => {
        const machineName = escapeHtml(assessment?.macchinarioNome || 'Macchinario sconosciuto');
        const activityDateSource = assessment?.dataAssessment || assessment?.createdAt || assessment?.dataCreazione || assessment?.createdDate;
        const assessmentDate = escapeHtml(formatDate(activityDateSource));
        const riskLabel = normalizeRiskLevelLabel(assessment?.livelloRischio) || 'N/D';
        const riskClass = getRiskClass(riskLabel);

        return `
            <li class="dashboard-activity-item">
                <div class="dashboard-activity-item__meta">
                    <strong class="dashboard-activity-item__machine">${machineName}</strong>
                    <span class="dashboard-activity-item__date">${assessmentDate}</span>
                </div>
                <span class="dashboard-risk-chip ${riskClass}">Rischio ${escapeHtml(riskLabel)}</span>
            </li>
        `;
    }).join('');
}

function renderDashboardOverview() {
    renderDashboardRiskChart();
    renderDashboardRecentActivity();
}

function getDocumentsCountFromTable() {
    if (!documentsTable) return 0;

    return Array.from(documentsTable.querySelectorAll('tr')).filter((row) => {
        if (row.classList.contains('documents-skeleton-row')) return false;
        if (row.getAttribute('data-pending-upload') === 'true') return false;
        return !row.querySelector('.empty-state');
    }).length;
}

function getAssessmentsStatsFromList() {
    if (!Array.isArray(assessmentsCache) || !assessmentsCache.length) {
        return { total: 0, conformi: 0 };
    }

    return {
        total: assessmentsCache.length,
        conformi: assessmentsCache.filter((assessment) => resolveAssessmentStatus(assessment) === 'CONFORME').length
    };
}

function normalizeAssessmentStatus(value) {
    if (typeof value !== 'string') return '';

    const normalized = value.trim()
        .toUpperCase()
        .replace(/[_-]+/g, ' ')
        .replace(/\s+/g, ' ');

    if (!normalized) return '';
    if (normalized === 'CONFORME') return 'CONFORME';
    if (normalized === 'NON CONFORME' || normalized === 'NONCONFORME') return 'NON_CONFORME';
    return '';
}

function resolveAssessmentStatus(assessment) {
    const directFields = ['statoConformita', 'stato', 'status', 'esito'];
    for (const field of directFields) {
        const normalizedStatus = normalizeAssessmentStatus(assessment?.[field]);
        if (normalizedStatus) return normalizedStatus;
    }

    const normalizedResult = normalizeAssessmentStatus(assessment?.risultato);
    if (normalizedResult) return normalizedResult;

    const resultText = typeof assessment?.risultato === 'string' ? assessment.risultato.toUpperCase() : '';
    if (/\bNON[\s_-]*CONFORME\b/.test(resultText)) return 'NON_CONFORME';
    if (/\bCONFORME\b/.test(resultText)) return 'CONFORME';
    return 'UNKNOWN';
}

function refreshDashboardStats(options = {}) {
    renderDashboardOverview();

    const snapshot = getDashboardStatsSnapshot();
    const statItems = getDashboardStatItems(snapshot);
    if (!statItems.length) return;

    const hasNonZeroValue = statItems.some((item) => item.value > 0);
    const shouldAnimateFromZero = (
        options.forceAnimateFromZero === true
        || (dashboardCountupPending && isDashboardSectionVisible() && hasNonZeroValue)
    );

    if (shouldAnimateFromZero) {
        dashboardCountupPending = false;
        animateDashboardStatsFromZero(statItems);
        return;
    }

    cancelDashboardCountupAnimation();
    renderDashboardStatsImmediate(statItems);
}

// Documents
function renderDocumentSkeletonRows(count = 3) {
    if (!documentsTable) return;

    const rows = Math.max(1, Math.floor(count));
    documentsTable.innerHTML = Array.from({ length: rows }, () => `
        <tr class="documents-skeleton-row" aria-hidden="true">
            <td colspan="6" class="documents-skeleton-cell">
                <div class="skeleton-row"></div>
            </td>
        </tr>
    `).join('');
}

async function loadDocuments() {
    if (!documentsTable) return;

    documentsTable.setAttribute('aria-busy', 'true');
    renderDocumentSkeletonRows(3);

    try {
        const response = await fetch(`${API_BASE}/documents`);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const payload = await response.json();
        const documents = Array.isArray(payload) ? payload : [];

        updateChatbotSubtitleFromDocuments(documents);

        if (documents.length === 0) {
            documentsTable.innerHTML = `
                <tr>
                    <td colspan="6" class="empty-state">
                        <div class="empty-state-icon empty-state-icon--document" aria-hidden="true">${DOCUMENT_EMPTY_STATE_ICON}</div>
                        <p>Nessun documento caricato</p>
                    </td>
                </tr>
            `;
            animateRowEnter(documentsTable, 'tr');
            bindRippleToButtons(documentsTable);
            refreshDashboardStats();
            refreshScrollDrivenEffects();
            return;
        }

        documentsTable.innerHTML = documents.map(doc => {
            const docId = Number(doc.id);
            const hasValidId = Number.isFinite(docId);
            const safeChunkCount = escapeHtml(doc.chunkCount ?? '-');
            return `
                <tr>
                    <td data-label="Nome File" class="doc-name-cell">${buildDocumentNameCell(doc.fileName)}</td>
                    <td data-label="Dimensione">${formatFileSize(doc.fileSize)}</td>
                    <td data-label="Data Caricamento">${formatDate(doc.uploadDate)}</td>
                    <td data-label="Stato">
                        <span class="badge ${doc.processed ? 'badge-success' : 'badge-warning'}">
                            ${doc.processed ? 'Processato' : 'In elaborazione'}
                        </span>
                    </td>
                    <td data-label="Chunks">${safeChunkCount}</td>
                    <td data-label="Azioni" class="actions-cell">
                        <button class="btn btn-sm btn-secondary" onclick="${hasValidId ? `downloadDocument(${docId})` : 'return false'}" title="Scarica documento" ${hasValidId ? '' : 'disabled'}>
                            Scarica
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="${hasValidId ? `deleteDocument(${docId})` : 'return false'}" title="Elimina documento" ${hasValidId ? '' : 'disabled'}>
                            Elimina
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
        animateRowEnter(documentsTable, 'tr');
        bindRippleToButtons(documentsTable);
        refreshDashboardStats();
        refreshScrollDrivenEffects();
    } catch (error) {
        console.error('Error loading documents:', error);
        documentsTable.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">
                    <div class="empty-state-icon empty-state-icon--error" aria-hidden="true">${DOCUMENT_ERROR_STATE_ICON}</div>
                    <p>Errore nel caricamento dei documenti</p>
                </td>
            </tr>
        `;
        animateRowEnter(documentsTable, 'tr');
        bindRippleToButtons(documentsTable);
        refreshDashboardStats();
        refreshScrollDrivenEffects();
    } finally {
        documentsTable.setAttribute('aria-busy', 'false');
    }
}

async function deleteDocument(id) {
    if (!confirm('Sei sicuro di voler eliminare questo documento?')) return;

    try {
        const response = await fetch(`${API_BASE}/documents/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            await loadDocuments();
            showToast('Documento eliminato con successo.', 'success');
        } else {
            showToast('Errore durante l\'eliminazione del documento.', 'error');
        }
    } catch (error) {
        console.error('Error deleting document:', error);
        showToast('Errore durante l\'eliminazione del documento.', 'error');
    }
}

function downloadDocument(id) {
    window.open(`${API_BASE}/documents/${id}/download`, '_blank');
}

async function downloadLatestAssessmentPdf(machineId) {
    try {
        const response = await fetch(`${API_BASE}/macchinari/${machineId}/assessments/latest/pdf`, {
            method: 'GET',
            headers: { 'Accept': 'application/pdf' },
            credentials: 'same-origin'
        });

        if (!response.ok) {
            const payload = await response.json().catch(() => ({}));
            alert(payload?.error || 'Impossibile scaricare il PDF');
            return;
        }

        const blob = await response.blob();
        const contentDisposition = response.headers.get('Content-Disposition') || '';
        const filename = getFilenameFromContentDisposition(contentDisposition) || `assessment_${machineId}.pdf`;

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error downloading assessment PDF:', error);
        alert('Errore durante il download del PDF');
    }
}

// Machines
function initMachineForm() {
    if (!machineForm) return;

    machineForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const machine = {
            nome: document.getElementById('machineName').value,
            produttore: document.getElementById('machineProducer').value,
            modello: document.getElementById('machineModel').value,
            numeroSerie: document.getElementById('machineSerial').value,
            annoProduzione: document.getElementById('machineYear').value ? parseInt(document.getElementById('machineYear').value) : null,
            categoria: document.getElementById('machineCategory').value,
            descrizione: document.getElementById('machineDescription').value,
            specificheTecniche: document.getElementById('machineSpecs').value
        };

        const isEdit = editingMachineId !== null;
        const editingMachineIdSnapshot = editingMachineId;
        const shouldMarkManualCompletion = machineManualUploadedInFormSession;
        const url = isEdit ? `${API_BASE}/macchinari/${editingMachineId}` : `${API_BASE}/macchinari`;
        const method = isEdit ? 'PUT' : 'POST';
        const submitBtn = machineSubmitBtn || machineForm.querySelector('button[type="submit"]');

        if (submitBtn) submitBtn.disabled = true;

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(machine)
            });

            if (response.ok) {
                const payload = await response.json().catch(() => ({}));
                let persistedMachineId = Number(payload?.id);
                if (!Number.isFinite(persistedMachineId) && isEdit) {
                    persistedMachineId = Number(editingMachineIdSnapshot);
                }

                if (isEdit) {
                    setMachineFormMode('create');
                    machineForm.reset();
                    loadAssessments();
                    showToast('Macchinario aggiornato con successo.', 'success');
                } else {
                    machineForm.reset();
                    showToast('Macchinario aggiunto con successo.', 'success');
                }

                await loadMachines();

                if (shouldMarkManualCompletion) {
                    if (!Number.isFinite(persistedMachineId)) {
                        persistedMachineId = findRecentlySavedMachineId(machine);
                    }
                    if (Number.isFinite(persistedMachineId)) {
                        setMachineManualCompletion(persistedMachineId, { refreshUi: false });
                    }
                }

                machineManualUploadedInFormSession = false;
                renderMachinesFromState();
            } else {
                const error = await response.json().catch(() => ({}));
                showToast(error.error || 'Errore durante il salvataggio', 'error');
            }
        } catch (error) {
            console.error('Error saving machine:', error);
            showToast('Errore durante il salvataggio', 'error');
        } finally {
            if (submitBtn) submitBtn.disabled = false;
        }
    });

    machineForm.addEventListener('reset', () => {
        machineManualUploadedInFormSession = false;
        if (editingMachineId !== null) {
            setMachineFormMode('create');
        }
    });
}

function initMachineSearch() {
    if (!machineSearchInput) return;

    machineSearchQuery = String(machineSearchInput.value || '').trim();
    machineSearchInput.addEventListener('input', () => {
        machineSearchQuery = String(machineSearchInput.value || '').trim();
        renderMachinesFromState();
    });
}

function clampGridZoomLevel(value) {
    const numericValue = Number(value);
    if (!Number.isFinite(numericValue)) return 1;
    return Math.max(GRID_ZOOM_MIN, Math.min(GRID_ZOOM_MAX, numericValue));
}

function getFilteredMachinesFromCache() {
    const allMachines = Array.isArray(machinesCache) ? machinesCache : [];
    const normalizedQuery = String(machineSearchQuery || '').trim().toLowerCase();
    return allMachines.filter((machine) => matchesMachineSearch(machine, normalizedQuery));
}

function getAssessmentsFromCache() {
    return Array.isArray(assessmentsCache) ? assessmentsCache : [];
}

function clampAssessmentGridZoomLevel(value) {
    const numericValue = Number(value);
    if (!Number.isFinite(numericValue)) return 1;
    return Math.max(ASSESSMENT_GRID_ZOOM_MIN, Math.min(ASSESSMENT_GRID_ZOOM_MAX, numericValue));
}

function clampAssessmentKanbanVisibleLimit(value) {
    const numericValue = Math.trunc(Number(value));
    if (!Number.isFinite(numericValue)) return ASSESSMENT_KANBAN_PREVIEW_DEFAULT;
    return Math.max(ASSESSMENT_KANBAN_PREVIEW_MIN, Math.min(ASSESSMENT_KANBAN_PREVIEW_MAX, numericValue));
}

function getAssessmentKanbanVisibleLimit() {
    return clampAssessmentKanbanVisibleLimit(assessmentKanbanVisibleLimit);
}

function syncAssessmentKanbanLimitInput() {
    if (!assessmentKanbanLimitInput) return;
    assessmentKanbanLimitInput.value = String(getAssessmentKanbanVisibleLimit());
}

function applyAssessmentKanbanVisibleLimit(rawValue, options = {}) {
    const nextValue = clampAssessmentKanbanVisibleLimit(rawValue);
    const changed = nextValue !== assessmentKanbanVisibleLimit;
    assessmentKanbanVisibleLimit = nextValue;
    syncAssessmentKanbanLimitInput();

    if (options.render === false) return;

    if (changed) {
        renderAssessmentsFromState();
    } else {
        updateAssessmentGridControls();
    }
}

function applyMachinesGridZoom() {
    if (!machinesGrid) return;
    const minWidth = Math.round(MACHINES_GRID_BASE_MIN_WIDTH * clampGridZoomLevel(machineGridZoomLevel));
    machinesGrid.style.setProperty('--machines-grid-min-width', `${minWidth}px`);
}

function applyAssessmentsGridZoom() {
    if (!assessmentsList) return;
    const normalizedZoom = clampAssessmentGridZoomLevel(assessmentGridZoomLevel);
    const trackWidth = Math.round(ASSESSMENTS_GRID_BASE_MIN_WIDTH * normalizedZoom);
    assessmentGridZoomLevel = normalizedZoom;
    assessmentsList.style.setProperty('--assessments-grid-min-width', `${trackWidth}px`);
    assessmentsList.style.setProperty('--assessments-grid-track-size', `${trackWidth}px`);
    assessmentsList.dataset.zoomLevel = String(Math.round(normalizedZoom * 100));
}

function setGridControlDisabled(button, disabled) {
    if (!button) return;
    button.disabled = disabled;
    button.setAttribute('aria-disabled', String(disabled));
}

function updateMachineGridControls(totalMachines = getFilteredMachinesFromCache().length) {
    const safeTotal = Math.max(0, Number(totalMachines) || 0);
    const isGrid = machinesViewMode === 'grid';
    const hiddenCount = Math.max(0, safeTotal - GRID_PREVIEW_LIMIT);
    const canExpand = isGrid && hiddenCount > 0;
    const isExpanded = canExpand && machineGridExpanded;

    machineGridControls?.classList.toggle('is-disabled', !isGrid);

    setGridControlDisabled(machineGridZoomOutBtn, !isGrid || machineGridZoomLevel <= (GRID_ZOOM_MIN + 0.001));
    setGridControlDisabled(machineGridZoomInBtn, !isGrid || machineGridZoomLevel >= (GRID_ZOOM_MAX - 0.001));

    if (machineGridExpandBtn) {
        setGridControlDisabled(machineGridExpandBtn, !canExpand);
        machineGridExpandBtn.classList.toggle('is-expanded', isExpanded);
        machineGridExpandBtn.setAttribute('aria-expanded', String(isExpanded));
        machineGridExpandBtn.setAttribute(
            'title',
            isExpanded
                ? `Mostra solo i primi ${GRID_PREVIEW_LIMIT}`
                : `Mostra altri ${hiddenCount} macchinari`
        );
    }

    if (machineGridInfo) {
        if (!isGrid) {
            machineGridInfo.textContent = 'Vista lista';
        } else if (canExpand && !isExpanded) {
            machineGridInfo.textContent = `${GRID_PREVIEW_LIMIT}/${safeTotal}`;
        } else {
            machineGridInfo.textContent = `${safeTotal}/${safeTotal}`;
        }
    }
}

function updateAssessmentGridControls(totalAssessments = getAssessmentsFromCache().length) {
    const safeTotal = Math.max(0, Number(totalAssessments) || 0);
    const isGrid = assessmentsViewMode === 'grid';
    const isKanban = assessmentsViewMode === 'kanban';
    const hiddenCount = Math.max(0, safeTotal - GRID_PREVIEW_LIMIT);
    const canExpand = isGrid && hiddenCount > 0;
    const isExpanded = canExpand && assessmentGridExpanded;
    const kanbanVisibleLimit = getAssessmentKanbanVisibleLimit();
    const kanbanVisibleCount = isKanban
        ? getAssessmentKanbanVisibleCount(getAssessmentsFromCache(), kanbanVisibleLimit)
        : 0;

    assessmentGridControls?.classList.toggle('is-disabled', !isGrid && !isKanban);
    assessmentGridControls?.classList.toggle('is-grid-mode', isGrid);
    assessmentGridControls?.classList.toggle('is-kanban-mode', isKanban);

    if (assessmentGridZoomOutBtn) {
        assessmentGridZoomOutBtn.hidden = !isGrid;
    }
    if (assessmentGridZoomInBtn) {
        assessmentGridZoomInBtn.hidden = !isGrid;
    }

    setGridControlDisabled(assessmentGridZoomOutBtn, !isGrid || assessmentGridZoomLevel <= (ASSESSMENT_GRID_ZOOM_MIN + 0.001));
    setGridControlDisabled(assessmentGridZoomInBtn, !isGrid || assessmentGridZoomLevel >= (ASSESSMENT_GRID_ZOOM_MAX - 0.001));

    if (assessmentGridExpandBtn) {
        assessmentGridExpandBtn.hidden = !isGrid;
        setGridControlDisabled(assessmentGridExpandBtn, !canExpand);
        assessmentGridExpandBtn.classList.toggle('is-expanded', isExpanded);
        assessmentGridExpandBtn.setAttribute('aria-expanded', String(isExpanded));
        assessmentGridExpandBtn.setAttribute(
            'title',
            isExpanded
                ? `Mostra solo i primi ${GRID_PREVIEW_LIMIT}`
                : `Mostra altri ${hiddenCount} assessment`
        );
    }

    if (assessmentKanbanControls) {
        if (isKanban) {
            assessmentKanbanControls.hidden = false;
            assessmentKanbanControls.setAttribute('aria-hidden', 'false');
        } else {
            assessmentKanbanControls.hidden = true;
            assessmentKanbanControls.setAttribute('aria-hidden', 'true');
        }
    }

    if (assessmentKanbanLimitInput) {
        assessmentKanbanLimitInput.disabled = !isKanban;
        syncAssessmentKanbanLimitInput();
    }

    if (assessmentKanbanApplyBtn) {
        assessmentKanbanApplyBtn.disabled = !isKanban;
    }

    if (assessmentGridInfo) {
        const zoomLabel = `${Math.round(clampAssessmentGridZoomLevel(assessmentGridZoomLevel) * 100)}%`;
        if (isGrid) {
            if (canExpand && !isExpanded) {
                assessmentGridInfo.textContent = `${GRID_PREVIEW_LIMIT}/${safeTotal} · zoom ${zoomLabel}`;
            } else {
                assessmentGridInfo.textContent = `${safeTotal}/${safeTotal} · zoom ${zoomLabel}`;
            }
        } else if (isKanban) {
            assessmentGridInfo.textContent = `${kanbanVisibleCount}/${safeTotal} · ${kanbanVisibleLimit}/col`;
        } else {
            assessmentGridInfo.textContent = 'Vista elenco';
        }
    }
}

function initGridDisplayControls() {
    applyMachinesGridZoom();
    applyAssessmentsGridZoom();
    applyAssessmentKanbanVisibleLimit(ASSESSMENT_KANBAN_PREVIEW_DEFAULT, { render: false });
    updateMachineGridControls();
    updateAssessmentGridControls();

    machineGridZoomOutBtn?.addEventListener('click', () => {
        if (machinesViewMode !== 'grid') return;
        machineGridZoomLevel = clampGridZoomLevel(machineGridZoomLevel - GRID_ZOOM_STEP);
        applyMachinesGridZoom();
        updateMachineGridControls();
    });

    machineGridZoomInBtn?.addEventListener('click', () => {
        if (machinesViewMode !== 'grid') return;
        machineGridZoomLevel = clampGridZoomLevel(machineGridZoomLevel + GRID_ZOOM_STEP);
        applyMachinesGridZoom();
        updateMachineGridControls();
    });

    machineGridExpandBtn?.addEventListener('click', () => {
        if (machinesViewMode !== 'grid') return;
        machineGridExpanded = !machineGridExpanded;
        renderMachinesFromState();
    });

    assessmentGridZoomOutBtn?.addEventListener('click', () => {
        if (assessmentsViewMode !== 'grid') return;
        assessmentGridZoomLevel = clampAssessmentGridZoomLevel(assessmentGridZoomLevel - ASSESSMENT_GRID_ZOOM_STEP);
        applyAssessmentsGridZoom();
        updateAssessmentGridControls();
    });

    assessmentGridZoomInBtn?.addEventListener('click', () => {
        if (assessmentsViewMode !== 'grid') return;
        assessmentGridZoomLevel = clampAssessmentGridZoomLevel(assessmentGridZoomLevel + ASSESSMENT_GRID_ZOOM_STEP);
        applyAssessmentsGridZoom();
        updateAssessmentGridControls();
    });

    assessmentGridExpandBtn?.addEventListener('click', () => {
        if (assessmentsViewMode !== 'grid') return;
        assessmentGridExpanded = !assessmentGridExpanded;
        renderAssessmentsFromState();
    });

    assessmentKanbanLimitInput?.addEventListener('change', () => {
        applyAssessmentKanbanVisibleLimit(assessmentKanbanLimitInput.value);
    });

    assessmentKanbanLimitInput?.addEventListener('keydown', (event) => {
        if (event.key !== 'Enter') return;
        event.preventDefault();
        applyAssessmentKanbanVisibleLimit(assessmentKanbanLimitInput.value);
    });

    assessmentKanbanApplyBtn?.addEventListener('click', () => {
        applyAssessmentKanbanVisibleLimit(assessmentKanbanLimitInput?.value);
    });
}

function applyMachinesViewMode(view, options = {}) {
    const nextView = view === 'grid' ? 'grid' : 'scroll';
    const isScroll = nextView === 'scroll';
    machinesViewMode = nextView;

    if (machinesGrid) {
        machinesGrid.classList.toggle('machines-grid--scroll', isScroll);
    }

    if (machineViewGridBtn) {
        const isActive = nextView === 'grid';
        machineViewGridBtn.classList.toggle('is-active', isActive);
        machineViewGridBtn.setAttribute('aria-pressed', String(isActive));
    }

    if (machineViewListBtn) {
        machineViewListBtn.classList.toggle('is-active', isScroll);
        machineViewListBtn.setAttribute('aria-pressed', String(isScroll));
    }

    if (options.persist) {
        try {
            localStorage.setItem(MACHINES_VIEW_STORAGE_KEY, nextView);
        } catch (_) {
            // ignore storage access issues
        }
    }

    applyMachinesGridZoom();
    updateMachineGridControls();

    if (options.refreshLayout !== false) {
        refreshScrollDrivenEffects();
    }
}

function initMachineViewToggle() {
    if (machineViewGridBtn) {
        machineViewGridBtn.addEventListener('click', () => {
            applyMachinesViewMode('grid', { persist: true });
            renderMachinesFromState();
        });
    }

    if (machineViewListBtn) {
        machineViewListBtn.addEventListener('click', () => {
            applyMachinesViewMode('scroll', { persist: true });
            renderMachinesFromState();
        });
    }
}

function applyAssessmentsViewMode(view, options = {}) {
    const nextView = (view === 'grid' || view === 'scroll' || view === 'kanban') ? view : 'scroll';
    const isScroll = nextView === 'scroll';
    const isGrid = nextView === 'grid';
    const isKanban = nextView === 'kanban';
    assessmentsViewMode = nextView;

    if (assessmentsList) {
        assessmentsList.classList.toggle('assessments-list--grid', isGrid);
        assessmentsList.classList.toggle('assessments-list--scroll', isScroll);
        assessmentsList.classList.toggle('assessments-list--kanban', isKanban);
    }

    if (assessmentViewGridBtn) {
        assessmentViewGridBtn.classList.toggle('is-active', isGrid);
        assessmentViewGridBtn.setAttribute('aria-pressed', String(isGrid));
    }

    if (assessmentViewScrollBtn) {
        assessmentViewScrollBtn.classList.toggle('is-active', isScroll);
        assessmentViewScrollBtn.setAttribute('aria-pressed', String(isScroll));
    }

    if (assessmentViewKanbanBtn) {
        assessmentViewKanbanBtn.classList.toggle('is-active', isKanban);
        assessmentViewKanbanBtn.setAttribute('aria-pressed', String(isKanban));
    }

    if (options.persist) {
        try {
            localStorage.setItem(ASSESSMENTS_VIEW_STORAGE_KEY, nextView);
        } catch (_) {
            // ignore storage access issues
        }
    }

    applyAssessmentsGridZoom();
    updateAssessmentGridControls();

    if (options.refreshLayout !== false) {
        refreshScrollDrivenEffects();
    }
}

function initAssessmentViewToggle() {
    if (assessmentViewGridBtn) {
        assessmentViewGridBtn.addEventListener('click', () => {
            applyAssessmentsViewMode('grid', { persist: true, refreshLayout: false });
            renderAssessmentsFromState();
            refreshScrollDrivenEffects();
        });
    }

    if (assessmentViewScrollBtn) {
        assessmentViewScrollBtn.addEventListener('click', () => {
            applyAssessmentsViewMode('scroll', { persist: true, refreshLayout: false });
            renderAssessmentsFromState();
            refreshScrollDrivenEffects();
        });
    }

    if (assessmentViewKanbanBtn) {
        assessmentViewKanbanBtn.addEventListener('click', () => {
            applyAssessmentsViewMode('kanban', { persist: true, refreshLayout: false });
            renderAssessmentsFromState();
            refreshScrollDrivenEffects();
        });
    }
}

function getAssessmentHeatmapWindowSize() {
    const numericWindow = Number(assessmentHeatmapWindowSize);
    if (!Number.isFinite(numericWindow)) return ASSESSMENT_HEATMAP_DEFAULT_WINDOW;
    return Math.max(ASSESSMENT_HEATMAP_MIN_WINDOW, Math.trunc(numericWindow));
}

function ensureAssessmentVelocityControls() {
    assessmentsVelocityMeta = assessmentsVelocityMeta || document.querySelector('#assessmentsVelocityCard .assessments-velocity-card__meta');
    const chartPanel = assessmentsVelocityChart?.closest('.assessments-insight-panel--xy');
    chartPanel?.querySelector('.assessments-velocity-toolbar')?.remove();
    assessmentsVelocityChart?.querySelector('.assessments-velocity-panbar')?.remove();
}

function updateAssessmentVelocityControls(velocityWindow = null) {
    if (!assessmentsVelocityMeta) {
        assessmentsVelocityMeta = document.querySelector('#assessmentsVelocityCard .assessments-velocity-card__meta');
    }
    if (!assessmentsVelocityMeta) return;
    assessmentsVelocityMeta.textContent = 'Ultime 8 settimane';
}

function hideAssessmentHeatmapTooltip() {
    if (assessmentHeatmapCanvas) {
        assessmentHeatmapCanvas.style.cursor = 'default';
    }
    if (!assessmentHeatmapTooltip) return;
    assessmentHeatmapTooltip.style.display = 'none';
    assessmentHeatmapTooltip.setAttribute('aria-hidden', 'true');
}

function initAssessmentHeatmapUi() {
    ensureAssessmentVelocityControls();

    if (assessmentHeatmapWindowSelect) {
        assessmentHeatmapWindowSize = ASSESSMENT_HEATMAP_DEFAULT_WINDOW;
        assessmentHeatmapWindowSelect.value = String(ASSESSMENT_HEATMAP_DEFAULT_WINDOW);
        assessmentHeatmapWindowSelect.disabled = true;
        assessmentHeatmapWindowSelect.setAttribute('aria-disabled', 'true');
        assessmentHeatmapWindowSelect.classList.add('is-static');
    }

    if (assessmentHeatmapCanvas) {
        assessmentHeatmapCanvas.addEventListener('mousemove', handleAssessmentHeatmapPointerMove);
        assessmentHeatmapCanvas.addEventListener('mouseleave', hideAssessmentHeatmapTooltip);
    }

    window.addEventListener('resize', () => {
        if (assessmentHeatmapResizeTimer !== null) {
            clearTimeout(assessmentHeatmapResizeTimer);
        }
        assessmentHeatmapResizeTimer = window.setTimeout(() => {
            assessmentHeatmapResizeTimer = null;
            renderAssessmentInsightsFromWindow();
        }, 120);
    });
}

function initChatbotCsvModalUi() {
    if (!chatbotCsvModal) return;

    chatbotCsvModal.addEventListener('click', (e) => {
        if (e.target === chatbotCsvModal) {
            closeChatbotCsvModal({ restoreFocus: true });
        }
    });

    chatbotCsvModalCloseBtn?.addEventListener('click', () => {
        closeChatbotCsvModal({ restoreFocus: true });
    });

    chatbotCsvCancelBtn?.addEventListener('click', () => {
        closeChatbotCsvModal({ restoreFocus: true });
    });

    chatbotCsvYesBtn?.addEventListener('click', () => {
        handleChatbotCsvRestartChoice(true);
    });

    chatbotCsvNoBtn?.addEventListener('click', () => {
        handleChatbotCsvRestartChoice(false);
    });

    document.addEventListener('keydown', (e) => {
        if (e.key !== 'Escape') return;
        if (!chatbotCsvModal.classList.contains('active')) return;
        closeChatbotCsvModal({ restoreFocus: true });
    });
}

// Chatbot (document RAG)
function initChatbot() {
    if (!chatbotMessages || !chatbotComposer || !chatbotInput) return;

    initChatbotSystemPromptSettings().catch((err) => console.error('Chatbot settings init error:', err));

    const thread = ensureChatbotThread();
    setChatbotConnectionState('online');
    renderChatbotThread();

    const shouldAnimateWelcome = Array.isArray(thread?.messages)
        && thread.messages.length === 1
        && thread.messages[0]?.role === 'assistant'
        && !thread.messages[0]?.pending;
    if (shouldAnimateWelcome) {
        startChatbotAssistantTypingEffect(thread.messages[0].id).catch(() => renderChatbotThread());
    }

    chatbotComposer.addEventListener('submit', (e) => {
        e.preventDefault();
        submitChatbotChat().catch((err) => console.error('Chatbot submit error:', err));
    });

    chatbotMessages.addEventListener('click', handleChatbotMessageAreaClick);
    chatbotMessages.addEventListener('input', handleChatbotMessageAreaInput);
    chatbotMessages.addEventListener('keydown', handleChatbotMessageAreaKeydown);

    chatbotInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            chatbotComposer.requestSubmit();
        }
    });

    chatbotInput.addEventListener('input', updateChatbotComposerState);

    if (chatbotNewChatBtn) {
        chatbotNewChatBtn.addEventListener('click', () => {
            openChatbotCsvModal();
        });
    }

    chatbotInput.setAttribute('data-max-lines', '5');
    chatbotInput.setAttribute('data-max-height', String(CHATBOT_TEXTAREA_MAX_HEIGHT));
    updateChatbotComposerState();
}

function openChatbotCsvModal() {
    if (!chatbotCsvModal) {
        restartChatbotConversationUi();
        return;
    }

    chatbotCsvModal.classList.add('active');
    chatbotCsvModal.setAttribute('aria-hidden', 'false');
    chatbotCsvYesBtn?.focus();
}

function closeChatbotCsvModal(options = {}) {
    if (!chatbotCsvModal) return;

    const restoreFocus = Boolean(options.restoreFocus);
    chatbotCsvModal.classList.remove('active');
    chatbotCsvModal.setAttribute('aria-hidden', 'true');

    if (restoreFocus) {
        chatbotNewChatBtn?.focus();
    }
}

function handleChatbotCsvRestartChoice(saveCsv) {
    const snapshot = saveCsv ? buildChatbotConversationSnapshot() : null;

    closeChatbotCsvModal();
    restartChatbotConversationUi();

    if (saveCsv) {
        const downloaded = downloadChatbotConversationCsv(snapshot);
        if (downloaded) {
            const count = snapshot?.messages?.length || 0;
            setChatbotExportStatus(`CSV scaricato correttamente (${count} messaggi esportati).`, 'info');
        } else {
            setChatbotExportStatus('Errore durante il download del CSV. Riprova.', 'muted');
        }
        return;
    }

    clearChatbotExportStatus();
}

function restartChatbotConversationUi() {
    setChatbotBusy(false);
    resetChatbotThread();
    setChatbotConnectionState('online');
    chatbotMessageEditingState = null;
    clearChatbotEditStatus();
    renderChatbotThread();
    chatbotInput?.focus();
    updateChatbotComposerState();
}

function setChatbotExportStatus(message, tone = 'info') {
    if (!chatbotExportStatus) return;

    if (chatbotExportStatusTimer) {
        clearTimeout(chatbotExportStatusTimer);
        chatbotExportStatusTimer = null;
    }

    const text = (message || '').toString().trim();
    chatbotExportStatus.textContent = text;
    chatbotExportStatus.classList.remove('is-visible', 'is-info', 'is-muted');

    if (!text) return;

    chatbotExportStatus.classList.add('is-visible');
    chatbotExportStatus.classList.add(tone === 'muted' ? 'is-muted' : 'is-info');
    chatbotExportStatusTimer = setTimeout(() => {
        clearChatbotExportStatus();
    }, 7000);
}

function clearChatbotExportStatus() {
    if (!chatbotExportStatus) return;

    if (chatbotExportStatusTimer) {
        clearTimeout(chatbotExportStatusTimer);
        chatbotExportStatusTimer = null;
    }

    chatbotExportStatus.textContent = '';
    chatbotExportStatus.classList.remove('is-visible', 'is-info', 'is-muted');
}

function setChatbotEditStatus(message, tone = 'info') {
    if (!chatbotEditStatus) return;
    const text = (message || '').toString().trim();
    chatbotEditStatus.textContent = text;
    chatbotEditStatus.classList.remove('is-visible', 'is-info', 'is-muted');

    if (!text) return;

    chatbotEditStatus.classList.add('is-visible');
    chatbotEditStatus.classList.add(tone === 'muted' ? 'is-muted' : 'is-info');
}

function clearChatbotEditStatus() {
    if (!chatbotEditStatus) return;
    chatbotEditStatus.textContent = '';
    chatbotEditStatus.classList.remove('is-visible', 'is-info', 'is-muted');
}

function setChatbotConnectionState(state = 'online') {
    const nextState = state === 'offline'
        ? 'offline'
        : (state === 'processing' ? 'processing' : 'online');
    const label = nextState === 'offline'
        ? 'offline'
        : (nextState === 'processing' ? 'in elaborazione' : 'online');

    chatbotConnectionState = nextState;

    if (!chatbotStatus) return;

    chatbotStatus.dataset.state = nextState;
    chatbotStatus.setAttribute('aria-label', `Stato chatbot: ${label}`);
    if (chatbotStatusLabel) {
        chatbotStatusLabel.textContent = label;
    }
}

function setChatbotBusy(isBusy, options = {}) {
    chatbotRequestInProgress = Boolean(isBusy);

    if (chatbotRequestInProgress) {
        if (chatbotInput) chatbotInput.disabled = true;
        if (chatbotNewChatBtn) chatbotNewChatBtn.disabled = true;
        setChatbotConnectionState('processing');
    } else {
        if (chatbotInput) chatbotInput.disabled = false;
        if (chatbotNewChatBtn) chatbotNewChatBtn.disabled = false;
        if (chatbotConnectionState === 'processing') {
            setChatbotConnectionState('online');
        }
    }

    updateChatbotComposerState();

    if (chatbotMessages) {
        chatbotMessages.setAttribute('aria-busy', chatbotRequestInProgress ? 'true' : 'false');
    }

    if (!chatbotRequestInProgress && options.focusInput) {
        chatbotInput?.focus();
    }
}

function normalizeChatbotMessage(message, options = {}) {
    const forceNotPending = Boolean(options.forceNotPending);
    const role = message?.role === 'user' ? 'user' : 'assistant';
    const rawTs = Number(message?.ts);
    return {
        id: message?.id || createChatMessageId(),
        role,
        text: (message?.text || '').toString(),
        ts: Number.isFinite(rawTs) ? rawTs : Date.now(),
        pending: forceNotPending ? false : Boolean(message?.pending),
        sources: role === 'assistant' && Array.isArray(message?.sources) ? [...message.sources] : []
    };
}

function collectChatbotUserTurnsFromIndex(messages, startIndex = 0) {
    const items = Array.isArray(messages) ? messages : [];
    const from = Math.max(0, Number(startIndex) || 0);
    return items
        .slice(from)
        .filter((msg) => msg?.role === 'user')
        .map((msg) => normalizeChatbotMessage(msg, { forceNotPending: true }))
        .filter((msg) => (msg.text || '').trim().length > 0);
}

function buildChatbotHistoryFromMessages(messages, maxMessages = 10) {
    const items = Array.isArray(messages) ? messages : [];
    return items
        .filter(m => !m?.pending)
        .filter(m => m?.role === 'user' || m?.role === 'assistant')
        .filter(m => (m?.text || '').trim().length > 0)
        .slice(-maxMessages)
        .map(m => ({ role: m.role, content: m.text }));
}

async function requestChatbotAnswer(question, history) {
    const normalizedQuestion = (question || '').toString().trim();
    if (!normalizedQuestion) {
        throw new Error('La domanda è obbligatoria');
    }

    const systemPrompt = loadChatbotSystemPromptOverride();
    const requestBody = {
        question: normalizedQuestion,
        history: Array.isArray(history) ? history : []
    };
    if (systemPrompt && systemPrompt.trim()) {
        requestBody.systemPrompt = systemPrompt;
    }

    const response = await fetch(`${API_BASE}/chatbot/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin',
        body: JSON.stringify(requestBody)
    });

    const payload = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(payload?.error || 'Errore durante la chat');
    }

    return {
        answer: (payload?.answer || '').toString().trim() || 'Risposta non disponibile.',
        sources: Array.isArray(payload?.sources) ? payload.sources : []
    };
}

function handleChatbotMessageAreaClick(event) {
    const copyBtn = event.target.closest?.('.chat-message-copy-btn');
    if (copyBtn) {
        const messageId = copyBtn.getAttribute('data-message-id') || '';
        copyChatbotMessageToClipboard(messageId).catch((err) => console.error('Clipboard copy error:', err));
        return;
    }

    if (chatbotRequestInProgress) return;

    const editBtn = event.target.closest?.('.chat-message-edit-btn');
    if (editBtn) {
        const messageId = editBtn.getAttribute('data-message-id') || '';
        beginChatbotMessageEditing(messageId);
        return;
    }

    const cancelBtn = event.target.closest?.('.chat-message-edit-cancel-btn');
    if (cancelBtn) {
        chatbotMessageEditingState = null;
        renderChatbotThread();
        return;
    }

    const saveBtn = event.target.closest?.('.chat-message-edit-save-btn');
    if (saveBtn) {
        const messageId = saveBtn.getAttribute('data-message-id') || '';
        applyChatbotMessageEditing(messageId).catch((err) => console.error('Chatbot edit regeneration error:', err));
    }
}

function handleChatbotMessageAreaInput(event) {
    if (chatbotRequestInProgress) return;

    const input = event.target.closest?.('.chat-message-edit-input');
    if (!input) return;

    const messageId = input.getAttribute('data-message-id') || '';
    if (!chatbotMessageEditingState || chatbotMessageEditingState.messageId !== messageId) return;

    chatbotMessageEditingState.draft = input.value || '';
    autoResizeChatInput(input);

    const messageEl = input.closest('.chat-message');
    const saveBtn = messageEl?.querySelector('.chat-message-edit-save-btn');
    if (saveBtn) {
        saveBtn.disabled = !(chatbotMessageEditingState.draft || '').trim();
    }
}

function handleChatbotMessageAreaKeydown(event) {
    if (chatbotRequestInProgress) return;

    const input = event.target.closest?.('.chat-message-edit-input');
    if (!input) return;

    if (event.key === 'Escape') {
        event.preventDefault();
        chatbotMessageEditingState = null;
        renderChatbotThread();
        return;
    }

    if (event.key !== 'Enter') return;
    if (!(event.ctrlKey || event.metaKey)) return;

    event.preventDefault();
    const messageId = input.getAttribute('data-message-id') || '';
    applyChatbotMessageEditing(messageId).catch((err) => console.error('Chatbot edit regeneration error:', err));
}

function beginChatbotMessageEditing(messageId) {
    if (chatbotRequestInProgress) return;
    if (!messageId) return;
    const thread = ensureChatbotThread();
    const message = thread?.messages?.find((m) => m?.id === messageId && m?.role === 'user' && !m?.pending);
    if (!message) return;

    chatbotMessageEditingState = {
        messageId,
        draft: (message.text || '').toString()
    };

    renderChatbotThread();

    requestAnimationFrame(() => {
        const input = chatbotMessages?.querySelector?.('.chat-message-edit-input');
        if (!input) return;
        input.focus();
        const length = input.value?.length || 0;
        input.setSelectionRange(length, length);
        autoResizeChatInput(input);
    });
}

async function applyChatbotMessageEditing(messageId) {
    if (chatbotRequestInProgress) return;
    if (!chatbotMessageEditingState || chatbotMessageEditingState.messageId !== messageId) return;

    const draft = (chatbotMessageEditingState.draft || '').trim();
    if (!draft) return;

    const thread = ensureChatbotThread();
    const messageIndex = thread.messages.findIndex((m) => m?.id === messageId && m?.role === 'user');
    if (messageIndex < 0) return;

    persistChatbotSystemPromptNow();
    chatbotMessageEditingState = null;
    setChatbotEditStatus('Rigenerazione automatica in corso…', 'info');
    renderChatbotThread();

    try {
        await regenerateChatbotConversationFromUserIndex(messageIndex, draft);
        setChatbotEditStatus('Risposte rigenerate in base al messaggio aggiornato.', 'info');
    } catch (error) {
        setChatbotEditStatus(`Rigenerazione non completata: ${error?.message || 'errore sconosciuto'}.`, 'muted');
        throw error;
    }
}

async function regenerateChatbotConversationFromUserIndex(userMessageIndex, editedText) {
    clearAllChatbotTypingEffects();

    const thread = ensureChatbotThread();
    const allMessages = Array.isArray(thread?.messages) ? thread.messages : [];
    if (!Number.isInteger(userMessageIndex) || userMessageIndex < 0 || userMessageIndex >= allMessages.length) {
        return;
    }

    const normalized = allMessages
        .map((msg) => normalizeChatbotMessage(msg, { forceNotPending: true }));

    const target = normalized[userMessageIndex];
    if (!target || target.role !== 'user') return;
    target.text = editedText;
    target.ts = Date.now();

    const preservedPrefix = normalized.slice(0, userMessageIndex);
    const turnsToReplay = collectChatbotUserTurnsFromIndex(normalized, userMessageIndex);

    thread.messages = preservedPrefix;
    setChatbotBusy(true);
    renderChatbotThread();

    try {
        for (let i = 0; i < turnsToReplay.length; i++) {
            const userTurn = turnsToReplay[i];
            const userMessage = {
                id: userTurn.id || createChatMessageId(),
                role: 'user',
                text: (userTurn.text || '').toString(),
                ts: Date.now(),
                pending: false,
                sources: []
            };
            thread.messages.push(userMessage);

            const pendingAssistant = {
                id: createChatMessageId(),
                role: 'assistant',
                text: i === 0
                    ? 'Rigenero la risposta in base al messaggio aggiornato…'
                    : 'Aggiorno la conversazione in base al nuovo contesto…',
                ts: Date.now(),
                pending: true,
                sources: []
            };
            thread.messages.push(pendingAssistant);
            renderChatbotThread();

            try {
                const history = buildChatbotHistoryFromMessages(thread.messages, 10);
                const result = await requestChatbotAnswer(userMessage.text, history);

                pendingAssistant.pending = false;
                pendingAssistant.text = result.answer;
                pendingAssistant.sources = result.sources;
                pendingAssistant.ts = Date.now();
                await startChatbotAssistantTypingEffect(pendingAssistant.id);
            } catch (error) {
                pendingAssistant.pending = false;
                pendingAssistant.text = `✗ ${error?.message || 'Errore durante la chat'}`;
                pendingAssistant.sources = [];
                pendingAssistant.ts = Date.now();
                setChatbotConnectionState('offline');
                await startChatbotAssistantTypingEffect(pendingAssistant.id);
                throw error;
            }
        }
        setChatbotConnectionState('online');
    } finally {
        setChatbotBusy(false, { focusInput: true });
    }
}

function buildChatbotConversationSnapshot() {
    const thread = ensureChatbotThread();
    const now = Date.now();

    const rawMessages = Array.isArray(thread?.messages) ? thread.messages : [];
    const messages = rawMessages
        .filter((msg) => !msg?.pending)
        .map((msg, index) => {
            const role = msg?.role === 'user' ? 'user' : 'assistant';
            const rawTs = Number(msg?.ts);
            return {
                role,
                text: (msg?.text || '').toString(),
                ts: Number.isFinite(rawTs) ? rawTs : now,
                originalIndex: index
            };
        })
        .sort((a, b) => (a.ts - b.ts) || (a.originalIndex - b.originalIndex))
        .map((msg, index) => ({
            index: index + 1,
            role: msg.role,
            text: msg.text,
            ts: msg.ts
        }));

    const startedAt = messages[0]?.ts || now;
    const endedAt = messages[messages.length - 1]?.ts || startedAt;

    return {
        startedAt,
        endedAt,
        exportedAt: now,
        messages
    };
}

function downloadChatbotConversationCsv(snapshot) {
    try {
        const data = snapshot || buildChatbotConversationSnapshot();
        const csv = buildChatbotConversationCsv(data);
        const filename = buildChatbotConversationCsvFilename(data);
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
        return true;
    } catch (error) {
        console.error('Error downloading chatbot CSV:', error);
        return false;
    }
}

function buildChatbotConversationCsv(snapshot) {
    const data = snapshot || buildChatbotConversationSnapshot();
    const start = formatCsvDateTime(data.startedAt);
    const end = formatCsvDateTime(data.endedAt);
    const exportedAt = formatCsvDateTime(data.exportedAt);

    const rows = [
        ['indice', 'ruolo', 'messaggio', 'data_ora_messaggio', 'data_ora_inizio_conversazione', 'data_ora_fine_conversazione', 'data_ora_export_csv']
    ];

    const messages = Array.isArray(data.messages) ? data.messages : [];
    if (messages.length === 0) {
        rows.push(['0', 'Sistema', 'Nessun messaggio disponibile', '', start, end, exportedAt]);
    } else {
        messages.forEach((msg) => {
            rows.push([
                String(msg.index || ''),
                msg.role === 'user' ? 'Utente' : 'Chatbot',
                (msg.text || '').toString(),
                formatCsvDateTime(msg.ts),
                start,
                end,
                exportedAt
            ]);
        });
    }

    const csvBody = rows
        .map((row) => row.map(escapeCsvValue).join(','))
        .join('\r\n');

    return `\uFEFF${csvBody}\r\n`;
}

function buildChatbotConversationCsvFilename(snapshot) {
    const data = snapshot || buildChatbotConversationSnapshot();
    return `chatbot_conversazione_${formatCsvFileStamp(data.startedAt)}.csv`;
}

function escapeCsvValue(value) {
    const normalized = (value ?? '')
        .toString()
        .replaceAll('\r\n', '\n')
        .replaceAll('\r', '\n')
        .replaceAll('\n', '\\n');
    return `"${normalized.replaceAll('"', '""')}"`;
}

function formatCsvDateTime(value) {
    const date = new Date(value || Date.now());
    if (Number.isNaN(date.getTime())) return '';

    return `${date.getFullYear()}-${pad2(date.getMonth() + 1)}-${pad2(date.getDate())} ${pad2(date.getHours())}:${pad2(date.getMinutes())}:${pad2(date.getSeconds())}`;
}

function formatCsvFileStamp(value) {
    const date = new Date(value || Date.now());
    if (Number.isNaN(date.getTime())) {
        return formatCsvFileStamp(Date.now());
    }
    return `${date.getFullYear()}${pad2(date.getMonth() + 1)}${pad2(date.getDate())}_${pad2(date.getHours())}${pad2(date.getMinutes())}${pad2(date.getSeconds())}`;
}

function pad2(number) {
    return String(number).padStart(2, '0');
}

async function initChatbotSystemPromptSettings() {
    if (!chatbotSystemPrompt) return;

    setChatbotPromptStatus('Caricamento…');

    chatbotDefaultSystemPrompt = await fetchChatbotDefaultSystemPrompt();

    const stored = loadChatbotSystemPromptOverride();
    if (stored) {
        chatbotSystemPrompt.value = stored;
        setChatbotPromptStatus('Personalizzato');
    } else if (chatbotDefaultSystemPrompt) {
        chatbotSystemPrompt.value = chatbotDefaultSystemPrompt;
        setChatbotPromptStatus('Predefinito');
    } else {
        setChatbotPromptStatus('');
    }

    chatbotSystemPrompt.addEventListener('input', () => {
        schedulePersistChatbotSystemPrompt();
        setChatbotPromptStatus('Salvataggio…');
    });

    if (chatbotPromptResetBtn) {
        chatbotPromptResetBtn.addEventListener('click', () => {
            if (!confirm('Vuoi ripristinare le istruzioni predefinite del chatbot?')) return;
            clearChatbotSystemPromptOverride();
            chatbotSystemPrompt.value = chatbotDefaultSystemPrompt || '';
            setChatbotPromptStatus('Predefinito');
        });
    }
}

async function fetchChatbotDefaultSystemPrompt() {
    try {
        const response = await fetch(`${API_BASE}/chatbot/system-prompt`, {
            method: 'GET',
            credentials: 'same-origin'
        });
        const payload = await response.json().catch(() => ({}));
        if (!response.ok) {
            return '';
        }
        return (payload?.systemPrompt || '').toString();
    } catch (error) {
        console.error('Error loading chatbot system prompt:', error);
        return '';
    }
}

function setChatbotPromptStatus(text) {
    if (!chatbotPromptStatus) return;
    chatbotPromptStatus.textContent = (text || '').toString();
}

function loadChatbotSystemPromptOverride() {
    try {
        const value = localStorage.getItem(CHATBOT_SYSTEM_PROMPT_STORAGE_KEY);
        if (!value) return null;
        return value.toString();
    } catch (_) {
        return null;
    }
}

function clearChatbotSystemPromptOverride() {
    try {
        localStorage.removeItem(CHATBOT_SYSTEM_PROMPT_STORAGE_KEY);
    } catch (_) {
        // ignore
    }
}

function schedulePersistChatbotSystemPrompt() {
    if (chatbotSystemPromptSaveTimer) {
        clearTimeout(chatbotSystemPromptSaveTimer);
    }
    chatbotSystemPromptSaveTimer = setTimeout(() => {
        persistChatbotSystemPromptNow();
        chatbotSystemPromptSaveTimer = null;
    }, 350);
}

function persistChatbotSystemPromptNow() {
    if (!chatbotSystemPrompt) return;

    const value = (chatbotSystemPrompt.value || '').toString();
    const trimmed = value.trim();
    const defaultTrimmed = (chatbotDefaultSystemPrompt || '').toString().trim();

    if (!trimmed) {
        clearChatbotSystemPromptOverride();
        setChatbotPromptStatus(defaultTrimmed ? 'Usa predefinito' : '');
        return;
    }

    if (defaultTrimmed && trimmed === defaultTrimmed) {
        clearChatbotSystemPromptOverride();
        setChatbotPromptStatus('Predefinito');
        return;
    }

    try {
        localStorage.setItem(CHATBOT_SYSTEM_PROMPT_STORAGE_KEY, value);
        setChatbotPromptStatus('Salvato');
    } catch (_) {
        setChatbotPromptStatus('Non salvato');
    }
}

function updateChatbotSubtitleFromDocuments(documents) {
    if (!chatbotSubtitle) return;

    const docs = Array.isArray(documents) ? documents : [];
    if (docs.length === 0) {
        chatbotSubtitle.textContent = 'Nessun documento caricato: carica PDF nella sezione Documenti.';
        return;
    }

    const processed = docs.filter(d => d?.processed).length;
    chatbotSubtitle.textContent = `Documenti disponibili: ${processed}/${docs.length} processati.`;
}

function setMachineFormMode(mode, machine) {
    if (mode === 'edit') {
        if (!machine) return;
        editingMachineId = machine.id;
        if (machineFormTitle) machineFormTitle.textContent = `Modifica Macchinario: ${machine.nome}`;
        if (machineSubmitBtn) machineSubmitBtn.textContent = 'Salva modifiche';
        if (machineResetBtn) machineResetBtn.textContent = 'Annulla';
        return;
    }

    // default: create mode
    editingMachineId = null;
    machineManualUploadedInFormSession = false;
    if (machineFormTitle) machineFormTitle.textContent = 'Nuovo Macchinario';
    if (machineSubmitBtn) machineSubmitBtn.textContent = 'Aggiungi Macchinario';
    if (machineResetBtn) machineResetBtn.textContent = 'Reset';
    setMachineManualStatus('', null);
}

function fillMachineForm(machine) {
    if (!machine || typeof machine !== 'object') return;

    const set = (elementId, value) => {
        const el = document.getElementById(elementId);
        if (!el) return;
        el.value = value ?? '';
    };

    set('machineName', machine.nome);
    set('machineProducer', machine.produttore);
    set('machineModel', machine.modello);
    set('machineSerial', machine.numeroSerie);
    set('machineYear', machine.annoProduzione);
    set('machineCategory', machine.categoria);
    set('machineDescription', machine.descrizione);
    set('machineSpecs', machine.specificheTecniche);
}

async function editMachine(id) {
    let machine = machinesById.get(id);

    if (!machine) {
        try {
            const response = await fetch(`${API_BASE}/macchinari/${id}`);
            if (response.ok) {
                machine = await response.json();
            }
        } catch (error) {
            console.error('Error loading machine:', error);
        }
    }

    if (!machine) {
        alert('Macchinario non trovato');
        return;
    }

    setMachineFormMode('edit', machine);
    fillMachineForm(machine);

    machineFormTitle?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    document.getElementById('machineName')?.focus();
}

function initMachineManualUpload() {
    if (!machineManualUploadBtn || !machineManualFileInput) return;

    machineManualUploadBtn.addEventListener('click', () => {
        machineManualFileInput.click();
    });

    machineManualFileInput.addEventListener('change', async () => {
        if (!machineManualFileInput.files || machineManualFileInput.files.length === 0) return;
        await uploadMachineManual(machineManualFileInput.files[0]);
    });
}

async function uploadMachineManual(file) {
    const isPdf = file.type === 'application/pdf' || file.name?.toLowerCase().endsWith('.pdf');
    if (!isPdf) {
        setMachineManualStatus('✗ Solo file PDF sono accettati', 'error');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    setMachineManualStatus('Analisi del manuale in corso...', 'loading');
    machineManualUploadBtn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/macchinari/manual/extract`, {
            method: 'POST',
            body: formData
        });

        const payload = await response.json().catch(() => ({}));

        if (!response.ok) {
            const errorMessage = payload?.error || 'Errore durante l\'analisi del manuale';
            throw new Error(errorMessage);
        }

        prefillMachineForm(payload);
        machineManualUploadedInFormSession = true;

        const editingId = Number(editingMachineId);
        if (Number.isFinite(editingId)) {
            setMachineManualCompletion(editingId);
        }

        setMachineManualStatus('✓ Campi pre-compilati. Verifica e poi salva.', 'success');
    } catch (error) {
        console.error('Manual upload error:', error);
        setMachineManualStatus(`✗ ${error.message || 'Errore durante l\'analisi del manuale'}`, 'error');
    } finally {
        machineManualUploadBtn.disabled = false;
        machineManualFileInput.value = '';
    }
}

function prefillMachineForm(data) {
    if (!data || typeof data !== 'object') return;

    const setIfPresent = (elementId, value) => {
        const el = document.getElementById(elementId);
        if (!el) return;
        if (value === null || value === undefined) return;
        const stringValue = typeof value === 'string' ? value.trim() : value;
        if (stringValue === '') return;
        el.value = stringValue;
    };

    setIfPresent('machineName', data.nome);
    setIfPresent('machineProducer', data.produttore);
    setIfPresent('machineModel', data.modello);
    setIfPresent('machineSerial', data.numeroSerie);
    setIfPresent('machineDescription', data.descrizione);
    setIfPresent('machineSpecs', data.specificheTecniche);

    if (data.annoProduzione !== null && data.annoProduzione !== undefined) {
        const year = parseInt(data.annoProduzione, 10);
        if (!Number.isNaN(year)) {
            setIfPresent('machineYear', year);
        }
    }

    if (data.categoria) {
        const categorySelect = document.getElementById('machineCategory');
        if (categorySelect) {
            const optionExists = Array.from(categorySelect.options).some(o => o.value === data.categoria);
            if (optionExists) {
                categorySelect.value = data.categoria;
            }
        }
    }
}

function setMachineManualStatus(message, status) {
    if (!machineManualStatus) return;

    machineManualStatus.textContent = message || '';
    machineManualStatus.classList.remove('status-loading', 'status-success', 'status-error');
    if (status) {
        machineManualStatus.classList.add(`status-${status}`);
    }
}

function renderMachineSkeletonCards(count = 3) {
    if (!machinesGrid) return;

    const cards = Math.max(1, Math.floor(count));
    machinesGrid.innerHTML = Array.from({ length: cards }, () => `
        <article class="skeleton-card machine-skeleton-card" aria-hidden="true">
            <div class="skeleton-row"></div>
            <div class="skeleton-row"></div>
            <div class="skeleton-row"></div>
            <div class="skeleton-row"></div>
        </article>
    `).join('');
    animateRowEnter(machinesGrid, '.skeleton-card');
}

function matchesMachineSearch(machine, query) {
    if (!query) return true;

    const haystack = [
        machine?.nome,
        machine?.produttore,
        machine?.modello,
        machine?.numeroSerie,
        machine?.categoria,
        machine?.descrizione,
        machine?.specificheTecniche
    ]
        .map((value) => String(value || '').toLowerCase())
        .join(' ');

    return haystack.includes(query);
}

function normalizeMachineNameKey(value) {
    return String(value || '').trim().toLowerCase();
}

function findRecentlySavedMachineId(machineDraft) {
    const targetName = normalizeMachineNameKey(machineDraft?.nome);
    const targetSerial = normalizeMachineNameKey(machineDraft?.numeroSerie);
    const targetModel = normalizeMachineNameKey(machineDraft?.modello);

    if (!targetName) return null;

    let bestId = null;
    const source = Array.isArray(machinesCache) ? machinesCache : [];
    source.forEach((machine) => {
        const nameMatches = normalizeMachineNameKey(machine?.nome) === targetName;
        if (!nameMatches) return;

        if (targetSerial && normalizeMachineNameKey(machine?.numeroSerie) !== targetSerial) return;
        if (targetModel && normalizeMachineNameKey(machine?.modello) !== targetModel) return;

        const numericId = Number(machine?.id);
        if (!Number.isFinite(numericId)) return;
        if (bestId === null || numericId > bestId) {
            bestId = numericId;
        }
    });

    return Number.isFinite(bestId) ? bestId : null;
}

function getAssessmentMachineId(assessment) {
    const idCandidates = [
        assessment?.macchinarioId,
        assessment?.machineId,
        assessment?.idMacchinario
    ];

    for (const candidate of idCandidates) {
        const numericId = Number(candidate);
        if (Number.isFinite(numericId)) {
            return numericId;
        }
    }
    return null;
}

function resolveAssessmentMachineType(assessment) {
    const directCandidates = [
        assessment?.macchinarioCategoria,
        assessment?.machineCategory,
        assessment?.categoriaMacchinario,
        assessment?.categoria,
        assessment?.tipoMacchinario,
        assessment?.machineType,
        assessment?.tipologia
    ];

    for (const candidate of directCandidates) {
        const normalized = String(candidate || '').trim();
        if (normalized) {
            return normalized;
        }
    }

    const machineId = getAssessmentMachineId(assessment);
    const machineNameKey = normalizeMachineNameKey(
        assessment?.macchinarioNome
        || assessment?.machineName
        || assessment?.nomeMacchinario
    );
    const source = Array.isArray(machinesCache) ? machinesCache : [];

    const matchedMachine = source.find((machine) => {
        if (Number.isFinite(machineId) && Number(machine?.id) === machineId) {
            return true;
        }
        if (machineNameKey && normalizeMachineNameKey(machine?.nome) === machineNameKey) {
            return true;
        }
        return false;
    });

    const resolved = matchedMachine?.categoria || matchedMachine?.tipo || matchedMachine?.tipologia || '';
    return String(resolved || '').trim() || 'Tipo non specificato';
}

function updateMachineAssessmentProgress(map, key, assessment, timestamp) {
    if (key === null || key === undefined || key === '') return;

    const current = map.get(key) || { count: 0, latestAssessment: null, latestTimestamp: -Infinity };
    const next = {
        count: current.count + 1,
        latestAssessment: current.latestAssessment,
        latestTimestamp: current.latestTimestamp
    };

    if (timestamp >= current.latestTimestamp) {
        next.latestAssessment = assessment;
        next.latestTimestamp = timestamp;
    }

    map.set(key, next);
}

function buildMachineAssessmentProgressLookup() {
    const byId = new Map();
    const byName = new Map();
    const source = Array.isArray(assessmentsCache) ? assessmentsCache : [];

    source.forEach((assessment) => {
        const timestamp = getAssessmentTimestamp(assessment);
        const machineId = getAssessmentMachineId(assessment);
        const machineNameKey = normalizeMachineNameKey(
            assessment?.macchinarioNome
            || assessment?.machineName
            || assessment?.nomeMacchinario
        );

        if (Number.isFinite(machineId)) {
            updateMachineAssessmentProgress(byId, machineId, assessment, timestamp);
        }
        if (machineNameKey) {
            updateMachineAssessmentProgress(byName, machineNameKey, assessment, timestamp);
        }
    });

    return { byId, byName };
}

function getMachineAssessmentProgress(machine, lookup) {
    const machineId = Number(machine?.id);
    const machineNameKey = normalizeMachineNameKey(machine?.nome);
    let progress = null;

    if (Number.isFinite(machineId)) {
        progress = lookup.byId.get(machineId) || null;
    }
    if (!progress && machineNameKey) {
        progress = lookup.byName.get(machineNameKey) || null;
    }

    const hasAssessment = Boolean(progress?.count);
    const latestAssessment = progress?.latestAssessment || null;
    const latestConforme = hasAssessment && resolveAssessmentStatus(latestAssessment) === 'CONFORME';
    const latestAssessmentTimestamp = hasAssessment ? getAssessmentTimestamp(latestAssessment) : null;
    const machineLastModifiedTimestamp = getMachineLastModifiedTimestamp(machine);
    const canGenerateAssessment = !hasAssessment
        || !Number.isFinite(latestAssessmentTimestamp)
        || (Number.isFinite(machineLastModifiedTimestamp) && machineLastModifiedTimestamp > latestAssessmentTimestamp);
    const hasFreshAssessment = hasAssessment && !canGenerateAssessment;

    return {
        hasAssessment,
        latestAssessment,
        latestConforme,
        canGenerateAssessment,
        hasFreshAssessment
    };
}

function getMachineLastModifiedTimestamp(machine) {
    const dateCandidates = [
        machine?.dataUltimaModifica,
        machine?.updatedAt,
        machine?.dataCreazione,
        machine?.createdAt
    ];

    for (const candidate of dateCandidates) {
        const parsed = parseAssessmentDateCandidate(candidate);
        if (parsed && Number.isFinite(parsed.getTime())) {
            return parsed.getTime();
        }
    }

    return null;
}

function getMachineGenerateButtonConfig(progress) {
    if (!progress?.hasAssessment) {
        return {
            label: 'Genera assessment',
            title: 'Genera il primo assessment per questo macchinario',
            disabled: false,
            className: ''
        };
    }

    if (progress.canGenerateAssessment) {
        return {
            label: 'Rigenera assessment',
            title: 'Genera un nuovo assessment aggiornato dopo le modifiche',
            disabled: false,
            className: 'is-generated is-regenerable'
        };
    }

    return {
        label: 'Assessment già generato',
        title: 'Modifica il macchinario per generarne un altro',
        disabled: true,
        className: 'is-generated is-locked'
    };
}

function renderMachineCompletionTrack(machine, lookup) {
    const manualUploaded = hasMachineManualCompletion(machine?.id);
    const assessmentProgress = getMachineAssessmentProgress(machine, lookup);
    const steps = [
        { label: 'Macchinario registrato', completed: true },
        { label: 'Manuale caricato', completed: manualUploaded },
        { label: 'Assessment generato', completed: assessmentProgress.hasAssessment },
        { label: 'Conforme', completed: assessmentProgress.latestConforme }
    ];

    const safeMachineName = escapeHtml(machine?.nome || 'macchinario');
    const stepsMarkup = steps.map((step, index) => {
        const isComplete = step.completed === true;
        const nextStep = steps[index + 1];
        const hasCompleteLink = Boolean(nextStep && step.completed && nextStep.completed);
        const stateClass = isComplete ? 'is-complete' : 'is-pending';
        const linkClass = hasCompleteLink ? 'is-link-complete' : '';

        return `
            <li class="machine-dod-step ${stateClass} ${linkClass}" data-step-index="${index + 1}">
                <span class="machine-dod-dot" aria-hidden="true"></span>
                <span class="machine-dod-label">${escapeHtml(step.label)}</span>
            </li>
        `;
    }).join('');

    return `
        <section class="machine-completion" aria-label="Stato completamento ${safeMachineName}">
            <h5>Stato completamento</h5>
            <ol class="machine-dod-track" aria-label="Checklist Definition of Done">
                ${stepsMarkup}
            </ol>
        </section>
    `;
}

function renderMachineCards(machines) {
    const progressLookup = buildMachineAssessmentProgressLookup();

    return machines.map((machine) => {
        const machineId = Number(machine?.id);
        const hasValidId = Number.isFinite(machineId);
        const safeName = escapeHtml(machine?.nome || 'Macchinario sconosciuto');
        const safeCategory = escapeHtml(machine?.categoria || 'Categoria non specificata');
        const safeProducer = escapeHtml(machine?.produttore || '-');
        const safeModel = escapeHtml(machine?.modello || '-');
        const safeSerial = escapeHtml(machine?.numeroSerie || '-');
        const safeYear = escapeHtml(machine?.annoProduzione || '-');
        const safeDescription = machine?.descrizione
            ? `<p class="machine-card-description">${escapeHtml(truncate(machine.descrizione, 100))}</p>`
            : '';
        const assessmentProgress = getMachineAssessmentProgress(machine, progressLookup);
        const generateButton = getMachineGenerateButtonConfig(assessmentProgress);
        const detailsEnabled = hasValidId && assessmentProgress.hasAssessment;
        const downloadEnabled = hasValidId && assessmentProgress.hasAssessment;
        const actionHint = assessmentProgress.hasFreshAssessment
            ? '<p class="machine-card-actions__hint">Modifica il macchinario per sbloccare una nuova generazione.</p>'
            : '';
        const completionMarkup = renderMachineCompletionTrack(machine, progressLookup);

        return `
        <div class="machine-card" data-machine-id="${hasValidId ? machineId : ''}">
            <div class="machine-card-header">
                <div class="machine-card-header__content">
                    <h4>${safeName}</h4>
                    <p>${safeCategory}</p>
                </div>
                <div class="machine-card-header__actions">
                    <button class="btn btn-sm btn-secondary machine-card-header-btn"
                            type="button"
                            onclick="${hasValidId ? `editMachine(${machineId})` : 'return false'}"
                            title="Modifica macchinario"
                            ${hasValidId ? '' : 'disabled'}>
                        Modifica
                    </button>
                    <button class="btn btn-sm btn-danger machine-card-header-btn"
                            type="button"
                            onclick="${hasValidId ? `deleteMachine(${machineId})` : 'return false'}"
                            title="Elimina macchinario"
                            ${hasValidId ? '' : 'disabled'}>
                        Elimina
                    </button>
                </div>
            </div>
            <div class="machine-card-body">
                <div class="machine-info">
                    <div class="machine-info-item">
                        <label>Produttore</label>
                        <span>${safeProducer}</span>
                    </div>
                    <div class="machine-info-item">
                        <label>Modello</label>
                        <span>${safeModel}</span>
                    </div>
                    <div class="machine-info-item">
                        <label>N. Serie</label>
                        <span>${safeSerial}</span>
                    </div>
                    <div class="machine-info-item">
                        <label>Anno</label>
                        <span>${safeYear}</span>
                    </div>
                </div>
                ${safeDescription}
                ${completionMarkup}
                <div class="machine-card-actions">
                    <button class="btn btn-sm btn-success machine-action-btn machine-action-btn--generate ${generateButton.className}"
                            type="button"
                            onclick="${hasValidId ? `generateAssessment(${machineId})` : 'return false'}"
                            title="${generateButton.title}"
                            ${hasValidId && !generateButton.disabled ? '' : 'disabled'}>
                        ${generateButton.label}
                    </button>
                    <button class="btn btn-sm btn-secondary machine-action-btn machine-action-btn--secondary ${assessmentProgress.hasAssessment ? 'is-available' : ''}"
                            type="button"
                            onclick="${detailsEnabled ? `viewMachineAssessments(${machineId})` : 'return false'}"
                            title="${detailsEnabled ? 'Apri il dettaglio dell\'ultimo assessment' : 'Genera prima un assessment'}"
                            ${detailsEnabled ? '' : 'disabled'}>
                        Vedi dettagli assesment
                    </button>
                    <button class="btn btn-sm btn-secondary machine-action-btn machine-action-btn--secondary ${assessmentProgress.hasAssessment ? 'is-available' : ''}"
                            type="button"
                            onclick="${downloadEnabled ? `downloadLatestAssessmentPdf(${machineId})` : 'return false'}"
                            title="${downloadEnabled ? 'Scarica l\'ultimo assessment in PDF' : 'Genera prima un assessment'}"
                            ${downloadEnabled ? '' : 'disabled'}>
                        Scarica assessment PDF
                    </button>
                </div>
                ${actionHint}
            </div>
        </div>
    `;
    }).join('');
}

function renderMachinesFromState() {
    if (!machinesGrid) return;
    if (machinesGrid.getAttribute('aria-busy') === 'true') return;

    const allMachines = Array.isArray(machinesCache) ? machinesCache : [];
    const query = String(machineSearchQuery || '').trim();
    const normalizedQuery = query.toLowerCase();

    if (allMachines.length === 0) {
        machinesGrid.innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <div class="empty-state-icon">🔧</div>
                <p>Nessun macchinario registrato</p>
            </div>
        `;
        animateRowEnter(machinesGrid, '.empty-state');
        bindRippleToButtons(machinesGrid);
        updateMachineGridControls(0);
        refreshScrollDrivenEffects();
        return;
    }

    const filteredMachines = allMachines.filter((machine) => matchesMachineSearch(machine, normalizedQuery));
    if (filteredMachines.length === 0) {
        machinesGrid.innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <div class="empty-state-icon">🔎</div>
                <p>Nessun macchinario trovato</p>
                <p style="font-size: 0.9rem;">Nessun risultato per "${escapeHtml(query)}"</p>
            </div>
        `;
        animateRowEnter(machinesGrid, '.empty-state');
        bindRippleToButtons(machinesGrid);
        updateMachineGridControls(0);
        refreshScrollDrivenEffects();
        return;
    }

    const shouldCollapseGridPreview = machinesViewMode === 'grid'
        && !machineGridExpanded
        && filteredMachines.length > GRID_PREVIEW_LIMIT;
    const machinesToRender = shouldCollapseGridPreview
        ? filteredMachines.slice(0, GRID_PREVIEW_LIMIT)
        : filteredMachines;

    machinesGrid.innerHTML = renderMachineCards(machinesToRender);
    animateRowEnter(machinesGrid, '.machine-card');
    bindRippleToButtons(machinesGrid);
    updateMachineGridControls(filteredMachines.length);
    refreshScrollDrivenEffects();
}

async function loadMachines() {
    if (!machinesGrid) return;

    machinesGrid.setAttribute('aria-busy', 'true');
    renderMachineSkeletonCards(3);

    try {
        const response = await fetch(`${API_BASE}/macchinari`);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }
        const payload = await response.json();
        const machines = Array.isArray(payload) ? payload : [];

        machinesCache = machines;
        machinesById = new Map((machines || []).map(m => [m.id, m]));
    } catch (error) {
        console.error('Error loading machines:', error);
        machinesCache = [];
        machinesById = new Map();
        machinesGrid.innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <div class="empty-state-icon">⚠️</div>
                <p>Errore nel caricamento dei macchinari</p>
            </div>
        `;
        animateRowEnter(machinesGrid, '.empty-state');
        bindRippleToButtons(machinesGrid);
        updateMachineGridControls(0);
        renderAssessmentInsightsFromWindow();
        refreshDashboardStats();
        refreshScrollDrivenEffects();
        return;
    } finally {
        machinesGrid.setAttribute('aria-busy', 'false');
    }

    renderMachinesFromState();
    renderAssessmentInsightsFromWindow();
    refreshDashboardStats();
}

async function generateAssessment(machineId) {
    const machine = machinesById.get(machineId) || null;
    if (machine) {
        const progress = getMachineAssessmentProgress(machine, buildMachineAssessmentProgressLookup());
        if (!progress.canGenerateAssessment) {
            showToast('Esiste già un assessment aggiornato. Modifica il macchinario prima di generarne un altro.', 'error');
            return;
        }
    }

    if (!confirm('Vuoi generare un nuovo assessment per questo macchinario?')) return;

    loadingOverlay.classList.add('active');

    try {
        const response = await fetch(`${API_BASE}/macchinari/${machineId}/assessment`, {
            method: 'POST'
        });

        loadingOverlay.classList.remove('active');

        if (response.ok) {
            const assessment = await response.json();
            showAssessmentModal(assessment);
            await loadAssessments();
            showToast('Assessment generato con successo.', 'success');
        } else {
            const error = await response.json().catch(() => ({}));
            showToast(error.error || 'Impossibile generare l\'assessment', 'error');
        }
    } catch (error) {
        loadingOverlay.classList.remove('active');
        console.error('Error generating assessment:', error);
        showToast('Errore durante la generazione dell\'assessment', 'error');
    }
}

async function deleteMachine(id) {
    if (!confirm('Sei sicuro di voler eliminare questo macchinario e tutti i suoi assessment?')) return;

    try {
        const response = await fetch(`${API_BASE}/macchinari/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadMachines();
            loadAssessments();
        } else {
            alert('Errore durante l\'eliminazione');
        }
    } catch (error) {
        console.error('Error deleting machine:', error);
    }
}

async function viewMachineAssessments(machineId) {
    try {
        const response = await fetch(`${API_BASE}/macchinari/${machineId}/assessments`);
        const assessments = await response.json();

        if (assessments.length === 0) {
            alert('Nessun assessment disponibile per questo macchinario');
            return;
        }

        // Show the first (most recent) assessment
        showAssessmentModal(assessments[0]);
    } catch (error) {
        console.error('Error loading assessments:', error);
    }
}

// Assessments
function buildAssessmentStandardCardMarkup(assessment) {
    const assessmentStatus = resolveAssessmentStatus(assessment);
    const normalizedScore = normalizeAssessmentScore(assessment?.punteggioConformita);
    const scoreValue = normalizedScore ?? 0;
    const scoreClass = getScoreClass(normalizedScore);
    const scoreLabel = normalizedScore === null ? 'N/D' : `${normalizedScore}/100`;
    const scoreGapLabel = normalizedScore === null ? 'Punteggio non disponibile' : `Gap al target: ${100 - normalizedScore} punti`;
    const scoreAria = normalizedScore === null
        ? 'Punteggio conformità non disponibile'
        : `Conformità ${normalizedScore} su 100, distanza dalla conformità totale ${100 - normalizedScore} punti`;
    const assessmentId = Number(assessment?.id);
    const hasValidId = Number.isFinite(assessmentId);
    const safeMachineName = escapeHtml(assessment?.macchinarioNome || 'Macchinario sconosciuto');
    const safeDate = escapeHtml(formatDate(assessment?.dataAssessment));
    const safeRisk = escapeHtml(normalizeRiskLevelLabel(assessment?.livelloRischio) || 'N/D');
    const safeSummary = escapeHtml(truncate(assessment?.riepilogoConformita || 'Non disponibile', 300));

    return `
        <div class="assessment-card" data-assessment-status="${assessmentStatus}">
            <div class="assessment-header">
                <div class="assessment-header__content">
                    <h4>${safeMachineName}</h4>
                    <p class="assessment-card-date">
                        ${safeDate}
                    </p>
                </div>
                <div class="assessment-score">
                    <span class="risk-badge ${getRiskClass(assessment?.livelloRischio)}">
                        Rischio ${safeRisk}
                    </span>
                    <div class="assessment-score-metric">
                        <div class="assessment-score-metric__row">
                            <span class="assessment-score-caption">Conformità</span>
                            <div class="assessment-progress"
                                 role="progressbar"
                                 aria-label="${escapeHtml(scoreAria)}"
                                 aria-valuemin="0"
                                 aria-valuemax="100"
                                 aria-valuenow="${scoreValue}">
                                <span class="assessment-progress-fill ${scoreClass}" style="width: ${scoreValue}%;"></span>
                            </div>
                            <span class="assessment-score-value ${scoreClass}">${scoreLabel}</span>
                        </div>
                        <span class="assessment-progress-text">${scoreGapLabel}</span>
                    </div>
                </div>
            </div>
            <div class="assessment-body">
                <div class="assessment-section">
                    <h5>Riepilogo Conformità</h5>
                    <p>${safeSummary}</p>
                </div>
                <div class="assessment-card-actions">
                    <button class="btn btn-sm assessment-action-btn assessment-action-btn--view"
                            onclick="${hasValidId ? `showAssessmentDetail(${assessmentId})` : 'return false'}"
                            aria-label="Apri dettagli assessment"
                            ${hasValidId ? '' : 'disabled'}>
                        <span class="assessment-action-btn__icon">${ASSESSMENT_DETAILS_ICON}</span>
                        <span>Dettagli</span>
                    </button>
                    <button class="btn btn-sm assessment-action-btn assessment-action-btn--delete delete-assessment-btn"
                            data-id="${hasValidId ? assessmentId : ''}"
                            data-name="${safeMachineName}"
                            aria-label="Elimina assessment"
                            ${hasValidId ? '' : 'disabled'}>
                        <span class="assessment-action-btn__icon">${ASSESSMENT_DELETE_ICON}</span>
                        <span>Elimina</span>
                    </button>
                </div>
            </div>
        </div>
    `;
}

function resolveAssessmentKanbanColumn(level) {
    const normalized = normalizeRiskLevelLabel(level);
    if (normalized === 'Basso' || normalized === 'Medio' || normalized === 'Alto' || normalized === 'Critico') {
        return normalized;
    }
    return 'Critico';
}

function getAssessmentKanbanColumnSubtitle(level) {
    switch (normalizeRiskLevelLabel(level)) {
        case 'Basso':
            return 'Monitoraggio ordinario';
        case 'Medio':
            return 'Attenzione pianificata';
        case 'Alto':
            return 'Azioni prioritarie';
        case 'Critico':
        default:
            return 'Intervento immediato';
    }
}

function buildAssessmentKanbanBuckets(assessments) {
    const buckets = new Map(ASSESSMENT_KANBAN_COLUMNS.map((column) => [column.key, []]));
    (Array.isArray(assessments) ? assessments : []).forEach((assessment) => {
        const bucketKey = resolveAssessmentKanbanColumn(assessment?.livelloRischio);
        const bucket = buckets.get(bucketKey);
        if (bucket) {
            bucket.push(assessment);
        }
    });
    return buckets;
}

function getAssessmentKanbanVisibleCount(assessments, visibleLimit = getAssessmentKanbanVisibleLimit()) {
    const safeLimit = clampAssessmentKanbanVisibleLimit(visibleLimit);
    const buckets = buildAssessmentKanbanBuckets(assessments);
    return ASSESSMENT_KANBAN_COLUMNS.reduce((total, column) => {
        const cards = buckets.get(column.key) || [];
        return total + Math.min(cards.length, safeLimit);
    }, 0);
}

function buildAssessmentKanbanCardMarkup(assessment) {
    const assessmentId = Number(assessment?.id);
    const hasValidId = Number.isFinite(assessmentId);
    const normalizedScore = normalizeAssessmentScore(assessment?.punteggioConformita);
    const scoreValue = normalizedScore ?? 0;
    const scoreClass = getScoreClass(normalizedScore);
    const scoreLabel = normalizedScore === null ? 'N/D' : `${normalizedScore}/100`;
    const scoreAria = normalizedScore === null
        ? 'Punteggio conformità non disponibile'
        : `Conformità ${normalizedScore} su 100`;
    const safeMachineName = escapeHtml(assessment?.macchinarioNome || 'Macchinario sconosciuto');
    const safeMachineType = escapeHtml(resolveAssessmentMachineType(assessment));
    const safeDate = escapeHtml(formatDate(assessment?.dataAssessment));
    const safeRisk = escapeHtml(normalizeRiskLevelLabel(assessment?.livelloRischio) || 'N/D');

    return `
        <article class="kanban-assessment-card" data-assessment-status="${resolveAssessmentStatus(assessment)}">
            <div class="kanban-assessment-card__top">
                <div class="kanban-assessment-card__heading">
                    <h4 title="${safeMachineName}">${safeMachineName}</h4>
                    <span class="kanban-assessment-card__type" title="${safeMachineType}">${safeMachineType}</span>
                </div>
                <span class="risk-badge ${getRiskClass(assessment?.livelloRischio)}">Rischio ${safeRisk}</span>
            </div>
            <div class="kanban-assessment-card__meta">
                <span class="kanban-assessment-card__date">${safeDate}</span>
            </div>
            <div class="kanban-assessment-card__metric">
                <div class="kanban-assessment-card__metric-row">
                    <span class="kanban-assessment-card__metric-label">Conformità</span>
                    <div class="assessment-progress"
                         role="progressbar"
                         aria-label="${escapeHtml(scoreAria)}"
                         aria-valuemin="0"
                         aria-valuemax="100"
                         aria-valuenow="${scoreValue}">
                        <span class="assessment-progress-fill ${scoreClass}" style="width: ${scoreValue}%;"></span>
                    </div>
                    <span class="assessment-score-value ${scoreClass}">${scoreLabel}</span>
                </div>
            </div>
            <div class="kanban-assessment-card__actions">
                <button class="btn btn-sm assessment-action-btn assessment-action-btn--view kanban-expand-btn"
                        onclick="${hasValidId ? `showAssessmentDetail(${assessmentId})` : 'return false'}"
                        aria-label="Apri dettagli assessment"
                        ${hasValidId ? '' : 'disabled'}>
                    <span class="assessment-action-btn__icon">${ASSESSMENT_DETAILS_ICON}</span>
                    <span>Dettagli</span>
                </button>
                <button class="btn btn-sm assessment-action-btn assessment-action-btn--delete delete-assessment-btn"
                        data-id="${hasValidId ? assessmentId : ''}"
                        data-name="${safeMachineName}"
                        aria-label="Elimina assessment"
                        ${hasValidId ? '' : 'disabled'}>
                    <span class="assessment-action-btn__icon">${ASSESSMENT_DELETE_ICON}</span>
                    <span>Elimina</span>
                </button>
            </div>
        </article>
    `;
}

function buildAssessmentsKanbanMarkup(assessments) {
    const buckets = buildAssessmentKanbanBuckets(assessments);
    const visibleLimit = getAssessmentKanbanVisibleLimit();

    return `
        <div class="assessments-kanban-board" role="list" aria-label="Kanban assessment per livello di rischio">
            ${ASSESSMENT_KANBAN_COLUMNS.map((column) => {
                const cards = buckets.get(column.key) || [];
                const visibleCards = cards.slice(0, visibleLimit);
                const hiddenCards = Math.max(0, cards.length - visibleCards.length);
                const subtitle = escapeHtml(getAssessmentKanbanColumnSubtitle(column.key));
                return `
                    <section class="kanban-column" data-risk="${column.key}">
                        <header class="kanban-column__header ${column.className}">
                            <div class="kanban-column__title-group">
                                <h3>${column.key}</h3>
                                <p class="kanban-column__subtitle">${subtitle}</p>
                            </div>
                            <div class="kanban-column__meta">
                                <span class="kanban-column__count">${cards.length}</span>
                                <span class="kanban-column__visible-count">${visibleCards.length}/${cards.length || 0}</span>
                            </div>
                        </header>
                        <div class="kanban-column__cards">
                            ${cards.length
                                ? visibleCards.map((assessment) => buildAssessmentKanbanCardMarkup(assessment)).join('')
                                : '<div class="kanban-column__empty">Nessuna card</div>'}
                        </div>
                        ${hiddenCards > 0
                            ? `<footer class="kanban-column__footer"><span class="kanban-column__more">Altri ${hiddenCards} assessment non mostrati</span></footer>`
                            : ''}
                    </section>
                `;
            }).join('')}
        </div>
    `;
}

function renderAssessmentsFromState() {
    if (!assessmentsList) return;
    if (assessmentsList.getAttribute('aria-busy') === 'true') return;
    const assessments = Array.isArray(assessmentsCache) ? assessmentsCache : [];

    if (assessments.length === 0) {
        assessmentsList.innerHTML = `
            <div class="empty-state assessment-empty-state">
                <div class="empty-state-icon" aria-hidden="true">${ASSESSMENT_EMPTY_STATE_ICON}</div>
                <p>Nessun assessment disponibile</p>
                <p class="assessment-empty-state__subtitle">Genera un assessment dalla sezione Macchinari</p>
            </div>
        `;
        animateRowEnter(assessmentsList, '.empty-state');
        updateAssessmentGridControls(0);
        return;
    }

    if (assessmentsViewMode === 'kanban') {
        assessmentsList.innerHTML = buildAssessmentsKanbanMarkup(assessments);
        animateRowEnter(assessmentsList, '.kanban-assessment-card, .kanban-column__empty, .kanban-column__more');
    } else {
        const shouldCollapseGridPreview = assessmentsViewMode === 'grid'
            && !assessmentGridExpanded
            && assessments.length > GRID_PREVIEW_LIMIT;
        const assessmentsToRender = shouldCollapseGridPreview
            ? assessments.slice(0, GRID_PREVIEW_LIMIT)
            : assessments;

        assessmentsList.innerHTML = assessmentsToRender
            .map((assessment) => buildAssessmentStandardCardMarkup(assessment))
            .join('');
        animateRowEnter(assessmentsList, '.assessment-card');
    }

    updateAssessmentGridControls(assessments.length);
}

function renderAssessmentSkeletonCards(count = 3) {
    if (!assessmentsList) return;

    const cards = Math.max(1, Math.floor(count));
    assessmentsList.innerHTML = Array.from({ length: cards }, () => `
        <article class="skeleton-card assessment-skeleton-card" aria-hidden="true">
            <div class="skeleton-row"></div>
            <div class="skeleton-row"></div>
            <div class="skeleton-row"></div>
            <div class="skeleton-row"></div>
        </article>
    `).join('');
    animateRowEnter(assessmentsList, '.skeleton-card');
}

async function loadAssessments() {
    if (assessmentsList) {
        assessmentsList.setAttribute('aria-busy', 'true');
        renderAssessmentSkeletonCards(3);
    }

    try {
        const response = await fetch(`${API_BASE}/assessments`);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }
        const payload = await response.json();
        const assessments = Array.isArray(payload) ? payload.slice() : [];
        assessments.sort((a, b) => getAssessmentTimestamp(b) - getAssessmentTimestamp(a));
        assessmentsCache = assessments;
        renderAssessmentsVelocityChart();
        renderAssessmentInsightsFromWindow();
        renderMachinesFromState();
        refreshDashboardStats();
    } catch (error) {
        console.error('Error loading assessments:', error);
        assessmentsCache = [];
        renderAssessmentsVelocityChart();
        renderAssessmentInsightsFromWindow();
        renderMachinesFromState();
        refreshDashboardStats();
    } finally {
        if (assessmentsList) {
            assessmentsList.setAttribute('aria-busy', 'false');
        }
    }

    renderAssessmentsFromState();
    refreshScrollDrivenEffects();
}

async function showAssessmentDetail(id) {
    try {
        const response = await fetch(`${API_BASE}/assessments/${id}`);
        const assessment = await response.json();
        showAssessmentModal(assessment);
    } catch (error) {
        console.error('Error loading assessment:', error);
    }
}

function showAssessmentModal(assessment) {
    document.getElementById('modalTitle').textContent = `Assessment: ${assessment.macchinarioNome}`;

    if (!assessmentDetails) {
        assessmentDetails = document.getElementById('assessmentDetails');
    }

    const assessmentId = Number(assessment?.id);
    currentAssessmentContext = Number.isFinite(assessmentId) ? {
        id: assessmentId,
        macchinarioId: assessment?.macchinarioId ?? null,
        macchinarioNome: assessment?.macchinarioNome ?? '',
        livelloRischio: assessment?.livelloRischio ?? null,
        punteggioConformita: assessment?.punteggioConformita ?? null,
        dataAssessment: assessment?.dataAssessment ?? null
    } : null;

    const nonConformitaItems = toFindingItems(assessment?.nonConformitaRilevate);
    const raccomandazioniItems = toFindingItems(assessment?.raccomandazioni);
    const conformityScore = normalizeAssessmentScore(assessment?.punteggioConformita);
    const conformityScoreLabel = conformityScore === null ? 'N/D' : `${conformityScore}/100`;
    const conformityScoreWidth = conformityScore ?? 0;
    const conformityScoreFillClass = getConformityScoreBarClass(conformityScore);
    const normalizedRiskLabel = normalizeRiskLevelLabel(assessment?.livelloRischio) || 'N/D';

    const chatThread = ensureAssessmentChatThread(assessmentId, assessment?.macchinarioNome);
    const chatSuggestions = buildAssessmentChatSuggestions(nonConformitaItems, raccomandazioniItems);

    assessmentDetails.innerHTML = `
        <div class="assessment-layout" data-assessment-id="${Number.isFinite(assessmentId) ? assessmentId : ''}" data-active-panel="results">
            <div class="assessment-layout__tabs" role="tablist" aria-label="Schede risultati/chat">
                <button type="button" class="assessment-tab is-active" data-panel="results" role="tab" aria-selected="true">
                    Risultati
                </button>
                <button type="button" class="assessment-tab" data-panel="chat" role="tab" aria-selected="false">
                    Chat
                </button>
            </div>

            <div class="assessment-layout__grid">
                <div class="assessment-results" data-panel="results" role="tabpanel">
                    <div class="assessment-score assessment-score--modal">
                        <div class="conformity-score-bar">
                            <div class="conformity-score-bar__label">Punteggio conformità: <strong>${conformityScoreLabel}</strong></div>
                            <div class="conformity-score-bar__track">
                                <div class="conformity-score-bar__fill ${conformityScoreFillClass}"
                                     data-target-width="${conformityScoreWidth}"
                                     style="width: ${conformityScoreWidth}%"></div>
                            </div>
                        </div>
                        <span class="risk-badge ${getRiskClass(assessment.livelloRischio)}">
                            Rischio ${escapeHtml(normalizedRiskLabel)}
                        </span>
                    </div>

                    <div class="assessment-section">
                        <h5>Riepilogo Conformità</h5>
                        <p>${assessment.riepilogoConformita || 'Non disponibile'}</p>
                    </div>

                    <div class="assessment-section">
                        <h5>Non Conformità Rilevate</h5>
                        ${renderFindingList(nonConformitaItems, 'nc', 'Nessuna non conformità rilevata')}
                    </div>

                    <div class="assessment-section">
                        <h5>Raccomandazioni</h5>
                        ${renderFindingList(raccomandazioniItems, 'rec', 'Nessuna raccomandazione')}
                    </div>

                    <div class="assessment-section">
                        <h5>Documenti Utilizzati</h5>
                        <p>${assessment.documentiUtilizzati || 'Nessun documento di riferimento'}</p>
                    </div>

                    <div class="assessment-meta">
                        <p>Data Assessment: ${formatDate(assessment.dataAssessment)}</p>
                        <p>Versione: ${assessment.versione || '1.0'}</p>
                    </div>
                </div>

                <aside class="assessment-chat" data-panel="chat" role="tabpanel" aria-label="Chat assistente sui risultati">
                    <div class="chat-header">
                        <div class="chat-header__title">
                            <span class="badge badge-info">AI</span>
                            <div>
                                <h3>Chat sui risultati</h3>
                                <p class="chat-header__subtitle">${escapeHtml(assessment?.macchinarioNome || 'Macchinario')}</p>
                            </div>
                        </div>
                        <button type="button" class="btn btn-sm btn-secondary chat-reset-btn" title="Nuova chat" aria-label="Nuova chat">
                            Nuova chat
                        </button>
                    </div>

                    <div class="chat-suggestions" aria-label="Domande rapide">
                        ${renderAssessmentChatSuggestionsHtml(chatSuggestions)}
                    </div>

                    <p class="chat-hint">
                        Suggerimento: usa <strong>Chat</strong> accanto alle non conformità per precompilare una domanda mirata.
                    </p>

                    <div class="chat-messages" data-chat-messages role="log" aria-live="polite" aria-relevant="additions">
                        ${renderAssessmentChatMessagesHtml(chatThread?.messages || [])}
                    </div>

                    <form class="chat-composer" autocomplete="off">
                        <label class="sr-only" for="assessmentChatInput">Messaggio</label>
                        <textarea id="assessmentChatInput"
                                  class="chat-input"
                                  data-chat-input="assessment"
                                  rows="1"
                                  placeholder="Fai una domanda su difetti e raccomandazioni…"></textarea>
                        <button type="submit" class="btn btn-primary chat-send-btn">Invia</button>
                    </form>
                </aside>
            </div>
        </div>
    `;

    assessmentModal.classList.add('active');

    requestAnimationFrame(() => {
        const layout = assessmentDetails?.querySelector?.('.assessment-layout');
        if (!layout || !Number.isFinite(assessmentId)) return;
        renderAssessmentChatIntoLayout(layout, assessmentId);
        refreshScrollDrivenEffects();
    });
}

function closeModal() {
    assessmentModal.classList.remove('active');
    currentAssessmentContext = null;
}

function setAssessmentActivePanel(layout, panel) {
    if (!layout || !panel) return;
    layout.setAttribute('data-active-panel', panel);

    const tabs = layout.querySelectorAll('.assessment-tab');
    tabs.forEach((tab) => {
        const isActive = tab.getAttribute('data-panel') === panel;
        tab.classList.toggle('is-active', isActive);
        tab.setAttribute('aria-selected', String(isActive));
    });

    if (panel === 'chat') {
        const input = layout.querySelector('.chat-input');
        if (input) {
            input.focus();
            input.setSelectionRange?.(input.value.length, input.value.length);
            autoResizeChatInput(input);
        }

        const messages = layout.querySelector('[data-chat-messages]');
        if (messages) {
            requestAnimationFrame(() => {
                try {
                    messages.scrollTop = messages.scrollHeight;
                } catch (_) {
                    // ignore
                }
            });
        }
    }
}

function prefillAssessmentChatInput(layout, question) {
    const input = layout?.querySelector?.('.chat-input');
    if (!input) return;

    input.value = question;
    autoResizeChatInput(input);
    input.focus();
    input.setSelectionRange?.(input.value.length, input.value.length);
}

function autoResizeChatInput(textarea) {
    if (!textarea) return;

    const maxHeightRaw = Number.parseFloat(textarea.getAttribute('data-max-height') || '');
    let max = Number.isFinite(maxHeightRaw) && maxHeightRaw > 0 ? maxHeightRaw : null;

    if (!max) {
        const maxLinesRaw = Number.parseInt(textarea.getAttribute('data-max-lines') || '5', 10);
        const maxLines = Number.isFinite(maxLinesRaw) && maxLinesRaw > 0 ? maxLinesRaw : 5;
        const style = window.getComputedStyle(textarea);
        const lineHeight = parseFloat(style.lineHeight) || 20;
        const paddingTop = parseFloat(style.paddingTop) || 0;
        const paddingBottom = parseFloat(style.paddingBottom) || 0;
        const borderTop = parseFloat(style.borderTopWidth) || 0;
        const borderBottom = parseFloat(style.borderBottomWidth) || 0;
        max = Math.round((lineHeight * maxLines) + paddingTop + paddingBottom + borderTop + borderBottom);
    }

    textarea.style.overflowY = 'hidden';
    textarea.style.height = 'auto';
    textarea.style.height = `${Math.min(textarea.scrollHeight, max)}px`;
    textarea.style.overflowY = textarea.scrollHeight > max ? 'auto' : 'hidden';
}

function updateChatbotComposerState() {
    if (!chatbotInput) return;

    autoResizeChatInput(chatbotInput);

    const textLength = chatbotInput.value?.length || 0;
    const isWarning = textLength > CHATBOT_CHAR_WARNING_THRESHOLD && textLength <= CHATBOT_CHAR_LIMIT;
    const isError = textLength > CHATBOT_CHAR_LIMIT;

    if (chatCharCounter) {
        chatCharCounter.textContent = `${textLength} / ${CHATBOT_CHAR_LIMIT}`;
        chatCharCounter.classList.toggle('is-warning', isWarning);
        chatCharCounter.classList.toggle('is-error', isError);
    }

    if (chatbotSendBtn) {
        chatbotSendBtn.disabled = chatbotRequestInProgress || isError;
    }
}

function createChatMessageId() {
    if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
        return crypto.randomUUID();
    }
    return `m_${Date.now()}_${Math.random().toString(16).slice(2)}`;
}

function ensureChatbotThread() {
    if (chatbotThread) return chatbotThread;

    chatbotThread = {
        messages: [{
            id: createChatMessageId(),
            role: 'assistant',
            text: [
                'Ciao! Posso rispondere usando i documenti PDF che hai caricato nella sezione "Documenti".',
                'Fammi una domanda e cercherò nei documenti i passaggi rilevanti.'
            ].join('\n'),
            ts: Date.now(),
            sources: []
        }]
    };

    return chatbotThread;
}

function resetChatbotThread() {
    clearAllChatbotTypingEffects();
    clearAllChatbotCopyFeedbackTimers();
    chatbotRenderedMessageIds.clear();
    chatbotThread = null;
    ensureChatbotThread();
}

function addChatbotMessage(role, text, options = {}) {
    ensureChatbotThread();
    const message = {
        id: createChatMessageId(),
        role: role === 'user' ? 'user' : 'assistant',
        text: text || '',
        ts: Date.now(),
        pending: Boolean(options.pending),
        sources: Array.isArray(options.sources) ? options.sources : []
    };
    chatbotThread.messages.push(message);
    return message.id;
}

function updateChatbotMessage(messageId, patch) {
    if (!chatbotThread || !Array.isArray(chatbotThread.messages)) return;
    const msg = chatbotThread.messages.find(m => m.id === messageId);
    if (!msg) return;
    Object.assign(msg, patch);
}

function getChatbotMessageById(messageId) {
    if (!messageId || !chatbotThread || !Array.isArray(chatbotThread.messages)) return null;
    return chatbotThread.messages.find((m) => m?.id === messageId) || null;
}

function clearChatbotTypingEffect(messageId, options = {}) {
    const effect = chatbotTypingEffects.get(messageId);
    if (!effect) return;

    if (effect.timerId) {
        clearInterval(effect.timerId);
    }
    chatbotTypingEffects.delete(messageId);

    const message = getChatbotMessageById(messageId);
    if (message && options.finalize !== false) {
        message.isTyping = false;
        message.displayedText = '';
    }

    if (typeof effect.resolve === 'function') {
        effect.resolve();
    }
}

function clearAllChatbotTypingEffects() {
    Array.from(chatbotTypingEffects.keys()).forEach((messageId) => clearChatbotTypingEffect(messageId));
}

function clearChatbotCopyFeedback(messageId) {
    const timer = chatbotCopyFeedbackTimers.get(messageId);
    if (!timer) return;
    clearTimeout(timer);
    chatbotCopyFeedbackTimers.delete(messageId);
}

function clearAllChatbotCopyFeedbackTimers() {
    Array.from(chatbotCopyFeedbackTimers.keys()).forEach((messageId) => clearChatbotCopyFeedback(messageId));
}

async function writeTextToClipboard(value) {
    const text = (value || '').toString();
    if (!text) return false;

    try {
        if (navigator?.clipboard?.writeText) {
            await navigator.clipboard.writeText(text);
            return true;
        }
    } catch (_) {
        // fallback below
    }

    try {
        const temp = document.createElement('textarea');
        temp.value = text;
        temp.setAttribute('readonly', 'readonly');
        temp.style.position = 'fixed';
        temp.style.opacity = '0';
        temp.style.pointerEvents = 'none';
        document.body.appendChild(temp);
        temp.focus();
        temp.select();
        const success = document.execCommand('copy');
        temp.remove();
        return Boolean(success);
    } catch (_) {
        return false;
    }
}

async function copyChatbotMessageToClipboard(messageId) {
    const message = getChatbotMessageById(messageId);
    if (!message || message.role !== 'assistant' || message.pending || message.isTyping) return;

    const rawText = (message.text || '').toString().trim();
    if (!rawText) return;

    const copied = await writeTextToClipboard(rawText);
    message.copyStatus = copied ? 'copied' : 'error';
    renderChatbotThread();

    clearChatbotCopyFeedback(messageId);
    const timer = setTimeout(() => {
        const current = getChatbotMessageById(messageId);
        if (!current) return;
        current.copyStatus = '';
        renderChatbotThread();
    }, copied ? 1800 : 2400);
    chatbotCopyFeedbackTimers.set(messageId, timer);
}

function computeTypingStep(textLength) {
    if (textLength > 1200) return 10;
    if (textLength > 800) return 8;
    if (textLength > 500) return 6;
    if (textLength > 260) return 4;
    return 2;
}

function startChatbotAssistantTypingEffect(messageId) {
    const message = getChatbotMessageById(messageId);
    if (!message || message.role !== 'assistant' || message.pending) {
        return Promise.resolve();
    }

    const fullText = (message.text || '').toString();
    clearChatbotTypingEffect(messageId);

    if (!fullText) {
        message.isTyping = false;
        message.displayedText = '';
        renderChatbotThread();
        return Promise.resolve();
    }

    message.isTyping = true;
    message.displayedText = '';
    message.copyStatus = '';
    renderChatbotThread();

    const step = computeTypingStep(fullText.length);
    let index = 0;

    return new Promise((resolve) => {
        const timerId = setInterval(() => {
            const current = getChatbotMessageById(messageId);
            if (!current || current.role !== 'assistant' || current.pending) {
                clearChatbotTypingEffect(messageId, { finalize: false });
                return;
            }

            index = Math.min(fullText.length, index + step);
            current.displayedText = fullText.slice(0, index);

            if (index >= fullText.length) {
                current.isTyping = false;
                current.displayedText = '';
                clearChatbotTypingEffect(messageId, { finalize: false });
            }

            renderChatbotThread();
        }, 24);

        chatbotTypingEffects.set(messageId, { timerId, resolve });
    });
}

function buildChatbotHistory(maxMessages = 10) {
    const messages = Array.isArray(chatbotThread?.messages) ? chatbotThread.messages : [];
    return buildChatbotHistoryFromMessages(messages, maxMessages);
}

function renderChatbotThread() {
    if (!chatbotMessages) return;

    const thread = ensureChatbotThread();
    chatbotMessages.innerHTML = renderChatbotMessagesHtml(thread?.messages || []);
    const messageIds = Array.isArray(thread?.messages)
        ? thread.messages.map((message) => message?.id).filter(Boolean)
        : [];
    messageIds.forEach((messageId) => chatbotRenderedMessageIds.add(messageId));

    requestAnimationFrame(() => {
        try {
            chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
        } catch (_) {
            // ignore
        }
    });
}

function renderChatbotMessagesHtml(messages) {
    const items = Array.isArray(messages) ? messages : [];
    return items.map(renderChatbotMessageHtml).join('');
}

function renderChatbotMessageSourcesHtml(sources) {
    const items = Array.isArray(sources) ? sources.filter(Boolean) : [];
    if (items.length === 0) return '';

    return `
        <details class="chatbot-sources">
            <summary>Fonti (${items.length})</summary>
            <div class="source-panel">
                ${renderSourcesPanel(items)}
            </div>
        </details>
    `;
}

function renderChatbotMessageHtml(message) {
    const role = message?.role === 'user' ? 'user' : 'assistant';
    const bubbleClass = role === 'user' ? 'chat-message--user' : 'chat-message--assistant';
    const pending = message?.pending ? ' <span class="chat-pending">•</span>' : '';
    const text = message?.text || '';
    const messageId = (message?.id || '').toString();
    const isNewMessage = messageId && !chatbotRenderedMessageIds.has(messageId);
    const enterClass = isNewMessage ? ' chat-message--enter' : '';
    const isTyping = role === 'assistant' && Boolean(message?.isTyping);
    const visibleText = isTyping ? (message?.displayedText || '') : text;
    const content = escapeHtml(visibleText).replaceAll('\n', '<br>');
    const typingCursor = isTyping ? '<span class="chat-typing-cursor" aria-hidden="true"></span>' : '';

    const label = role === 'user' ? 'Tu' : 'Assistente';
    const time = message?.ts ? new Date(message.ts).toLocaleTimeString('it-IT', { hour: '2-digit', minute: '2-digit' }) : '';
    const meta = `${label}${time ? ' · ' + time : ''}`;

    const isEditing = role === 'user' && chatbotMessageEditingState?.messageId === message?.id;
    const safeMessageId = escapeHtml(messageId);
    const lockAttribute = chatbotRequestInProgress ? ' disabled' : '';
    const assistantAvatar = '<span class="chat-message__avatar" aria-hidden="true">&#129302;</span>';

    let bodyHtml = role === 'assistant'
        ? `<div class="chat-message__row">${assistantAvatar}<div class="chat-bubble">${content}${typingCursor}${pending}</div></div>`
        : `<div class="chat-bubble">${content}${typingCursor}${pending}</div>`;
    let actionsHtml = '';

    if (role === 'user' && !message?.pending) {
        if (isEditing) {
            const draft = chatbotMessageEditingState?.draft ?? text;
            const draftSafe = escapeHtml(draft);
            const disableSave = draft.trim().length === 0 || chatbotRequestInProgress ? ' disabled' : '';
            bodyHtml = `
                <div class="chat-bubble chat-bubble--editing">
                    <label class="sr-only" for="chatbotEditInput-${safeMessageId}">Modifica messaggio</label>
                    <textarea id="chatbotEditInput-${safeMessageId}" class="chat-message-edit-input" data-message-id="${safeMessageId}" rows="2" data-max-lines="5"${lockAttribute}>${draftSafe}</textarea>
                </div>
            `;
            actionsHtml = `
                <div class="chat-message-actions">
                    <button type="button" class="chat-message-action-btn chat-message-edit-cancel-btn" data-message-id="${safeMessageId}"${lockAttribute}>
                        Annulla
                    </button>
                    <button type="button" class="chat-message-action-btn chat-message-action-btn--primary chat-message-edit-save-btn" data-message-id="${safeMessageId}"${disableSave}>
                        Aggiorna e rigenera
                    </button>
                </div>
            `;
        } else {
            actionsHtml = `
                <div class="chat-message-actions">
                    <button type="button" class="chat-message-action-btn chat-message-edit-btn" data-message-id="${safeMessageId}" aria-label="Modifica messaggio inviato" title="Modifica messaggio"${lockAttribute}>
                        Modifica
                    </button>
                </div>
            `;
        }
    } else if (role === 'assistant' && !message?.pending) {
        const copyState = message?.copyStatus || '';
        const copyLabel = copyState === 'copied'
            ? 'Copiato'
            : (copyState === 'error' ? 'Copia non riuscita' : 'Copia');
        const copyStateClass = copyState ? ` chat-message-copy-btn--${escapeHtml(copyState)}` : '';
        const copyDisabled = isTyping ? ' disabled' : '';
        actionsHtml = `
            <div class="chat-message-actions chat-message-actions--assistant">
                <button type="button" class="chat-message-action-btn chat-message-copy-btn${copyStateClass}" data-message-id="${safeMessageId}" aria-label="Copia risposta negli appunti"${copyDisabled}>
                    ${copyLabel}
                </button>
            </div>
        `;
    }

    const sourcesHtml = role === 'assistant' && !isTyping ? renderChatbotMessageSourcesHtml(message?.sources) : '';

    return `
        <div class="chat-message ${bubbleClass}${enterClass}">
            ${bodyHtml}
            <div class="chat-meta">${escapeHtml(meta)}</div>
            ${actionsHtml}
            ${sourcesHtml}
        </div>
    `;
}

async function submitChatbotChat() {
    if (!chatbotInput) return;
    if (chatbotRequestInProgress) return;
    if ((chatbotInput.value?.length || 0) > CHATBOT_CHAR_LIMIT) return;

    const text = chatbotInput.value?.trim?.() || '';
    if (!text) return;

    clearAllChatbotTypingEffects();
    persistChatbotSystemPromptNow();
    clearChatbotEditStatus();
    chatbotMessageEditingState = null;

    addChatbotMessage('user', text);
    chatbotInput.value = '';
    updateChatbotComposerState();

    const pendingId = addChatbotMessage('assistant', 'Sto cercando nei documenti…', { pending: true });
    renderChatbotThread();

    setChatbotBusy(true);
    renderChatbotThread();

    try {
        const history = buildChatbotHistory(10);
        const result = await requestChatbotAnswer(text, history);

        updateChatbotMessage(pendingId, {
            pending: false,
            text: result.answer,
            sources: result.sources
        });
        setChatbotConnectionState('online');
    } catch (error) {
        updateChatbotMessage(pendingId, {
            pending: false,
            text: `✗ ${error?.message || 'Errore durante la chat'}`,
            sources: []
        });
        setChatbotConnectionState('offline');
    } finally {
        setChatbotBusy(false, { focusInput: true });
        const pendingMessage = getChatbotMessageById(pendingId);
        if (pendingMessage) {
            startChatbotAssistantTypingEffect(pendingId).catch(() => renderChatbotThread());
        } else {
            renderChatbotThread();
        }
    }
}

function ensureAssessmentChatThread(assessmentId, macchinarioNome) {
    if (!Number.isFinite(assessmentId)) return null;

    let thread = assessmentChatThreads.get(assessmentId);
    if (thread) return thread;

    thread = {
        assessmentId,
        macchinarioNome: macchinarioNome || '',
        messages: [{
            id: createChatMessageId(),
            role: 'assistant',
            text: [
                'Ciao! Sono qui per aiutarti a capire i difetti trovati e come risolverli.',
                'Fammi una domanda sulle non conformità o sulle raccomandazioni visibili in questa pagina.'
            ].join('\n'),
            ts: Date.now()
        }]
    };

    assessmentChatThreads.set(assessmentId, thread);
    return thread;
}

function resetAssessmentChatThread(assessmentId) {
    const machineName = assessmentChatThreads.get(assessmentId)?.macchinarioNome || currentAssessmentContext?.macchinarioNome || '';
    assessmentChatThreads.delete(assessmentId);
    ensureAssessmentChatThread(assessmentId, machineName);
}

function addAssessmentChatMessage(assessmentId, role, text, options = {}) {
    const thread = ensureAssessmentChatThread(assessmentId, currentAssessmentContext?.macchinarioNome || '');
    if (!thread) return null;

    const message = {
        id: createChatMessageId(),
        role,
        text,
        ts: Date.now(),
        pending: Boolean(options.pending)
    };
    thread.messages.push(message);
    return message.id;
}

function updateAssessmentChatMessage(assessmentId, messageId, patch) {
    const thread = assessmentChatThreads.get(assessmentId);
    if (!thread) return;
    const msg = thread.messages.find(m => m.id === messageId);
    if (!msg) return;
    Object.assign(msg, patch);
}

function buildAssessmentChatHistory(assessmentId, maxMessages = 10) {
    const thread = assessmentChatThreads.get(assessmentId);
    const messages = Array.isArray(thread?.messages) ? thread.messages : [];

    return messages
        .filter(m => !m?.pending)
        .filter(m => m?.role === 'user' || m?.role === 'assistant')
        .filter(m => (m?.text || '').trim().length > 0)
        .slice(-maxMessages)
        .map(m => ({ role: m.role, content: m.text }));
}

async function submitAssessmentChat(form) {
    const layout = form.closest('.assessment-layout');
    const assessmentId = Number(layout?.getAttribute('data-assessment-id'));
    if (!layout || !Number.isFinite(assessmentId)) return;

    const input = form.querySelector('.chat-input');
    const text = input?.value?.trim?.() || '';
    if (!text) return;

    ensureAssessmentChatThread(assessmentId, currentAssessmentContext?.macchinarioNome || '');
    const history = buildAssessmentChatHistory(assessmentId, 10);

    input.value = '';
    autoResizeChatInput(input);

    addAssessmentChatMessage(assessmentId, 'user', text);
    const pendingId = addAssessmentChatMessage(assessmentId, 'assistant', 'Sto elaborando…', { pending: true });

    renderAssessmentChatIntoLayout(layout, assessmentId);

    const sendBtn = form.querySelector('.chat-send-btn');
    if (sendBtn) sendBtn.disabled = true;
    if (input) input.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/assessments/${assessmentId}/chat`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify({ question: text, history })
        });

        const payload = await response.json().catch(() => ({}));
        if (!response.ok) {
            throw new Error(payload?.error || 'Errore durante la chat');
        }

        const answer = (payload?.answer || '').toString().trim();
        updateAssessmentChatMessage(assessmentId, pendingId, {
            pending: false,
            text: answer || 'Risposta non disponibile.'
        });
    } catch (error) {
        updateAssessmentChatMessage(assessmentId, pendingId, {
            pending: false,
            text: `✗ ${error?.message || 'Errore durante la chat'}`
        });
    } finally {
        if (input) {
            input.disabled = false;
            input.focus();
        }
        if (sendBtn) sendBtn.disabled = false;
        renderAssessmentChatIntoLayout(layout, assessmentId);
    }
}

function renderAssessmentChatIntoLayout(layout, assessmentId) {
    const messagesEl = layout?.querySelector?.('[data-chat-messages]');
    if (!messagesEl) return;

    const thread = ensureAssessmentChatThread(assessmentId, currentAssessmentContext?.macchinarioNome || '');
    messagesEl.innerHTML = renderAssessmentChatMessagesHtml(thread?.messages || []);

    requestAnimationFrame(() => {
        try {
            messagesEl.scrollTop = messagesEl.scrollHeight;
        } catch (_) {
            // ignore
        }
    });
}

function renderAssessmentChatMessagesHtml(messages) {
    const items = Array.isArray(messages) ? messages : [];
    return items.map(renderAssessmentChatMessageHtml).join('');
}

function renderAssessmentChatMessageHtml(message) {
    const role = message?.role === 'user' ? 'user' : 'assistant';
    const bubbleClass = role === 'user' ? 'chat-message--user' : 'chat-message--assistant';
    const pending = message?.pending ? ' <span class="chat-pending">•</span>' : '';
    const text = message?.text || '';
    const content = escapeHtml(text).replaceAll('\n', '<br>');
    const time = message?.ts ? new Date(message.ts).toLocaleTimeString('it-IT', { hour: '2-digit', minute: '2-digit' }) : '';
    return `
        <div class="chat-message ${bubbleClass}">
            <div class="chat-bubble">${content}${pending}</div>
            <div class="chat-meta">${escapeHtml(time)}</div>
        </div>
    `;
}

function buildAssessmentChatSuggestions(nonConformitaItems, raccomandazioniItems) {
    const suggestions = [];
    const nonConformita = Array.isArray(nonConformitaItems) ? nonConformitaItems : [];
    const raccomandazioni = Array.isArray(raccomandazioniItems) ? raccomandazioniItems : [];

    const topFinding = nonConformita.length > 0 ? normalizeFinding(nonConformita[0]).text : '';
    if (topFinding) {
        suggestions.push({
            label: 'Spiega la principale non conformità',
            question: buildQuestionForFinding(topFinding)
        });
    }

    suggestions.push({
        label: 'Priorità interventi',
        question: 'Quali sono le priorità di intervento e i rischi principali in base a questo assessment?'
    });

    if (raccomandazioni.length > 0) {
        suggestions.push({
            label: 'Piano di azione',
            question: 'Puoi propormi un piano di azione step-by-step per risolvere le non conformità e applicare le raccomandazioni?'
        });
    }

    if (nonConformita.length > 1) {
        const secondFinding = normalizeFinding(nonConformita[1]).text;
        if (secondFinding) {
            suggestions.push({
                label: 'Spiega un\'altra non conformità',
                question: buildQuestionForFinding(secondFinding)
            });
        }
    }

    return suggestions.slice(0, 4);
}

function renderAssessmentChatSuggestionsHtml(suggestions) {
    const items = Array.isArray(suggestions) ? suggestions : [];
    if (items.length === 0) return '';
    return items.map(s => {
        const label = truncate(s.label || 'Domanda rapida', 42);
        const question = s.question || '';
        return `
            <button type="button" class="chat-suggestion" data-question="${escapeHtml(question)}" title="${escapeHtml(question)}">
                ${escapeHtml(label)}
            </button>
        `;
    }).join('');
}

// Utility functions
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('it-IT', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function truncate(str, length) {
    if (!str) return '';
    return str.length > length ? str.substring(0, length) + '...' : str;
}

function normalizeAssessmentScore(score) {
    const numericScore = Number(score);
    if (!Number.isFinite(numericScore)) return null;
    return Math.max(0, Math.min(100, Math.round(numericScore)));
}

function getScoreClass(score) {
    const normalizedScore = normalizeAssessmentScore(score);
    if (normalizedScore === null) return 'score-unknown';
    if (normalizedScore >= 70) return 'score-high';
    if (normalizedScore >= 40) return 'score-medium';
    return 'score-low';
}

function getConformityScoreBarClass(score) {
    const normalizedScore = normalizeAssessmentScore(score);
    if (normalizedScore === null) return 'conformity-score-bar__fill--low';
    if (normalizedScore >= 80) return 'conformity-score-bar__fill--high';
    if (normalizedScore >= 50) return 'conformity-score-bar__fill--medium';
    return 'conformity-score-bar__fill--low';
}

function getRiskClass(level) {
    const normalizedLevel = String(level || '').trim().toLowerCase();
    if (normalizedLevel.startsWith('bass')) return 'risk-basso';
    if (normalizedLevel.startsWith('med')) return 'risk-medio';
    if (normalizedLevel.startsWith('alt')) return 'risk-alto';
    if (normalizedLevel.startsWith('crit')) return 'risk-critico';
    return 'risk-sconosciuto';
}

function getFilenameFromContentDisposition(headerValue) {
    if (!headerValue) return null;

    const filenameStarMatch = headerValue.match(/filename\*=UTF-8''([^;]+)/i);
    if (filenameStarMatch?.[1]) {
        try {
            return decodeURIComponent(filenameStarMatch[1]);
        } catch (_) {
            return filenameStarMatch[1];
        }
    }

    const filenameMatch = headerValue.match(/filename=\"?([^\";]+)\"?/i);
    if (filenameMatch?.[1]) {
        return filenameMatch[1];
    }

    return null;
}

function escapeHtml(value) {
    if (value === null || value === undefined) return '';
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

function toFindingItems(raw) {
    if (!raw) return [];
    if (Array.isArray(raw)) return raw;

    if (typeof raw !== 'string') return [raw];

    const text = raw.trim();
    if (!text) return [];

    try {
        const parsed = JSON.parse(text);
        if (Array.isArray(parsed)) return parsed;
    } catch (_) {
        // ignore
    }

    return text
        .split(/\r?\n/)
        .map(line => line.trim())
        .filter(Boolean)
        .map(line => line
            .replace(/^[-*•]\s+/, '')
            .replace(/^\d+[.)]\s+/, '')
            .trim())
        .filter(Boolean);
}

function normalizeFinding(finding) {
    if (typeof finding === 'string') {
        return { text: finding, sources: [] };
    }

    if (finding && typeof finding === 'object') {
        const rawText = finding.testo || finding.text || finding.descrizione || finding.messaggio || '';
        const text = rawText === null || rawText === undefined ? '' : String(rawText);
        const sources = finding.fonti || finding.sources || finding.fonte || [];
        const sourcesArray = Array.isArray(sources) ? sources : [sources];
        return { text, sources: sourcesArray.filter(Boolean) };
    }

    return { text: String(finding ?? ''), sources: [] };
}

function normalizeConfidencePercent(raw) {
    const num = Number(raw);
    if (!Number.isFinite(num)) return null;

    if (num > 1) {
        return Math.max(0, Math.min(100, num));
    }

    return Math.max(0, Math.min(1, num)) * 100;
}

function getConfidenceClass(percent) {
    if (percent === null) return 'confidence-unknown';
    if (percent >= 80) return 'confidence-high';
    if (percent >= 50) return 'confidence-medium';
    return 'confidence-low';
}

function renderConfidenceBadge(confidence) {
    const percent = normalizeConfidencePercent(confidence);
    const label = percent === null ? 'Pertinenza: N/D' : `Pertinenza: ${Math.round(percent)}%`;
    return `<span class="confidence-badge ${getConfidenceClass(percent)}">🎯 ${escapeHtml(label)}</span>`;
}

function renderSourceCard(source, index) {
    if (!source || typeof source !== 'object') {
        return `
            <div class="source-card">
                <div class="source-panel-header">
                    <div class="source-title">📎 Fonte ${index + 1}</div>
                    <div class="source-meta">${renderConfidenceBadge(null)}</div>
                </div>
                <p class="source-ref"><strong>Riferimento:</strong> ${escapeHtml(source || '—')}</p>
                <div class="chunk-box"><span class="source-placeholder">— Testo chunk non disponibile</span></div>
            </div>
        `;
    }

    const reference = source.riferimento || source.reference || source.citazione || source.citation || source.ref || source.documentReference || source.documento || '—';
    const chunk = source.chunk || source.estratto || source.excerpt || source.testo || source.text || '—';
    const confidence = source.confidence ?? source.score ?? source.similarity ?? source.pertinenza ?? null;

    return `
        <div class="source-card">
            <div class="source-panel-header">
                <div class="source-title">📎 Fonte ${index + 1}</div>
                <div class="source-meta">${renderConfidenceBadge(confidence)}</div>
            </div>
            <p class="source-ref"><strong>Riferimento:</strong> ${escapeHtml(reference)}</p>
            <div class="chunk-box">${escapeHtml(chunk)}</div>
        </div>
    `;
}

function renderSourcesPanel(sources) {
    const normalized = Array.isArray(sources) ? sources.filter(Boolean) : [];

    if (normalized.length === 0) {
        return `
            <div class="source-panel-header">
                <div class="source-title">📎 Fonte normativa</div>
                <div class="source-meta">${renderConfidenceBadge(null)}</div>
            </div>
            <p class="source-ref"><strong>Riferimento:</strong> <span class="source-placeholder">— (es: Regolamento UE 2023/1230, Art. 12, c. 3)</span></p>
            <div class="chunk-box"><span class="source-placeholder">— Qui comparirà il testo esatto del regolamento (chunk recuperato dal RAG)</span></div>
        `;
    }

    return normalized.map((s, idx) => renderSourceCard(s, idx)).join('');
}

function renderFindingList(findings, groupId, emptyMessage = 'Nessuna segnalazione') {
    const items = Array.isArray(findings) ? findings : [];

    if (items.length === 0) {
        return `<p>${escapeHtml(emptyMessage)}</p>`;
    }

    return `
        <ul class="finding-list">
            ${items.map((item, index) => {
        const normalized = normalizeFinding(item);
        const panelId = `source-${groupId}-${index}`;
        const findingText = normalized.text?.trim() ? normalized.text : `Segnalazione ${index + 1}`;
        const sourceLabel = normalized.sources.length > 0 ? `Fonte (${normalized.sources.length})` : 'Fonte';
        const askQuestion = buildQuestionForFinding(findingText);
        const findingVariantClass = groupId === 'nc'
            ? 'finding-item--issue'
            : groupId === 'rec'
                ? 'finding-item--recommendation'
                : 'finding-item--neutral';
        return `
                    <li class="finding-item ${findingVariantClass}">
                        <div class="finding-row">
                            <div class="finding-text">${escapeHtml(findingText)}</div>
                            <div class="finding-actions">
                                <button type="button"
                                        class="btn btn-sm btn-secondary ask-assistant-btn"
                                        title="Chiedi all'assistente su questa segnalazione"
                                        data-question="${escapeHtml(askQuestion)}">
                                    Apri chat
                                </button>
                                <button type="button"
                                        class="btn btn-sm btn-secondary source-btn"
                                        title="Mostra fonte normativa"
                                        aria-expanded="false"
                                        aria-controls="${escapeHtml(panelId)}">
                                    ${escapeHtml(sourceLabel)}
                                </button>
                            </div>
                        </div>
                        <div id="${escapeHtml(panelId)}" class="source-panel" hidden>
                            ${renderSourcesPanel(normalized.sources)}
                        </div>
                    </li>
                `;
    }).join('')}
        </ul>
    `;
}

function buildQuestionForFinding(findingText) {
    const text = String(findingText || '').trim();
    if (!text) return 'Puoi spiegarmi meglio questa segnalazione e come risolverla?';
    return `Puoi spiegarmi meglio questa segnalazione e come risolverla? "${text}"`;
}
