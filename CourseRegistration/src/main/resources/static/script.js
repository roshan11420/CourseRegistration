const state = {
  students: [],
  courses: [],
  enrollments: [],
  currentUser: null,
  currentRole: null,
  editingCourseId: null
};

const toast = document.getElementById('toast');

function getStoredCredentials() {
  const username = document.getElementById('usernameInput')?.value?.trim();
  const password = document.getElementById('passwordInput')?.value?.trim();
  if (!username || !password) return null;
  return { username, password };
}

function getAuthHeader() {
  const credentials = getStoredCredentials();
  if (!credentials) return {};
  const token = btoa(`${credentials.username}:${credentials.password}`);
  return { Authorization: `Basic ${token}` };
}

const api = {
  async get(url) {
    const response = await fetch(url, {
      method: 'GET',
      headers: getAuthHeader(),
      credentials: 'same-origin'
    });
    if (!response.ok) {
      const payload = await response.json().catch(() => null);
      throw new Error(payload?.error || payload?.message || 'Request failed');
    }
    return response.json();
  },
  async post(url, body) {
    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(body),
      credentials: 'same-origin'
    });
    const payload = await response.json().catch(() => null);
    if (!response.ok) {
      throw new Error(payload?.error || payload?.details || payload?.message || 'Request failed');
    }
    return payload;
  },
  async delete(url) {
    const response = await fetch(url, {
      method: 'DELETE',
      headers: getAuthHeader(),
      credentials: 'same-origin'
    });
    if (!response.ok) {
      const payload = await response.json().catch(() => null);
      throw new Error(payload?.error || payload?.message || 'Delete request failed');
    }
  }
};

function showToast(message) {
  toast.textContent = message;
  toast.classList.add('visible');
  clearTimeout(showToast.timeoutId);
  showToast.timeoutId = setTimeout(() => toast.classList.remove('visible'), 2200);
}

function applyRoleAccess() {
  const value = state.currentRole;
  const adminPanels = document.querySelectorAll('.admin-only');
  const studentPanels = document.querySelectorAll('.student-only');

  adminPanels.forEach(panel => panel.classList.toggle('hidden-section', value !== 'ADMIN'));
  studentPanels.forEach(panel => panel.classList.toggle('hidden-section', value !== 'STUDENT'));

  if (value === 'ADMIN') {
    document.getElementById('usernameInput').value = 'admin';
    document.getElementById('passwordInput').value = 'admin123';
  } else if (value === 'STUDENT') {
    document.getElementById('usernameInput').value = 'student';
    document.getElementById('passwordInput').value = 'student123';
  }
}

async function loginAs(role) {
  const username = role === 'ADMIN' ? 'admin' : 'student';
  const password = role === 'ADMIN' ? 'admin123' : 'student123';
  document.getElementById('usernameInput').value = username;
  document.getElementById('passwordInput').value = password;
  state.currentRole = role;
  state.currentUser = username;
  applyRoleAccess();
  await refreshAll();
}

function showLogin() {
  document.getElementById('loginScreen').classList.remove('hidden-section');
  document.getElementById('appShell').classList.add('hidden-section');
  document.getElementById('authPanel').classList.add('hidden-section');
  document.getElementById('profilePanel')?.classList.add('hidden-section');
  document.getElementById('logoutBtn').classList.add('hidden-section');
  // clear inputs
  document.getElementById('loginUsername').value = '';
  document.getElementById('loginPassword').value = '';
  document.getElementById('usernameInput').value = '';
  document.getElementById('passwordInput').value = '';
  state.currentRole = null;
  state.currentUser = null;
}

function hideLogin() {
  document.getElementById('loginScreen').classList.add('hidden-section');
  document.getElementById('appShell').classList.remove('hidden-section');
  document.getElementById('authPanel').classList.remove('hidden-section');
  document.getElementById('logoutBtn').classList.remove('hidden-section');
  document.getElementById('profilePanel')?.classList.remove('hidden-section');
  updateProfileDisplay();
}

function updateProfileDisplay() {
  const profileNameEl = document.getElementById('profileName');
  if (!profileNameEl) return;

  // prefer student profile name if available
  let display = state.currentUser || '';
  if (state.currentRole === 'ADMIN') {
    display = 'Admin';
  } else if (state.currentRole === 'STUDENT') {
    // try to find student by username or email
    const byUser = state.students.find(s => String(s.username || s.email).toLowerCase() === String(state.currentUser).toLowerCase());
    const byName = state.students.find(s => String(s.name).toLowerCase() === String(state.currentUser).toLowerCase());
    if (byUser) display = byUser.name;
    else if (byName) display = byName.name;
    // else if registration used a different username, try to match recent created student with same username as id (not reliable)
  }

  profileNameEl.textContent = display;
}

async function loadDashboard() {
  const stats = await api.get('/api/dashboard');
  document.getElementById('studentCount').textContent = stats.students ?? 0;
  document.getElementById('courseCount').textContent = stats.courses ?? 0;
  document.getElementById('enrollmentCount').textContent = stats.enrollments ?? 0;
  document.getElementById('availableSeats').textContent = stats.availableSeats ?? 0;
}

async function loadStudents() {
  state.students = await api.get('/api/students');
  renderStudentTable();
  populateStudentSelect();
}

async function loadCourses() {
  state.courses = await api.get('/api/courses');
  renderCourseTable();
  populateCourseSelect();
}

async function loadEnrollments() {
  state.enrollments = await api.get('/api/enrollments');
  renderEnrollmentTable();
}

function renderStudentTable() {
  const body = document.getElementById('studentTableBody');
  body.innerHTML = state.students.map(student => `
    <tr>
      <td>${student.id}</td>
      <td>${student.name}</td>
      <td>${student.email}</td>
      <td>${student.department}</td>
      <td>${state.currentRole === 'ADMIN' ? `<button class="delete-btn" data-id="${student.id}" data-type="student">Delete</button>` : ''}</td>
    </tr>
  `).join('');
}

function renderCourseTable() {
  const body = document.getElementById('courseTableBody');
  body.innerHTML = state.courses.map(course => `
    <tr>
      <td>${course.code}</td>
      <td>${course.title}</td>
      <td>${course.department}</td>
      <td>${course.credits}</td>
      <td>${course.capacity ?? 30}</td>
      <td>
        ${state.currentRole === 'ADMIN' ? `<button class="edit-btn" data-id="${course.id}" data-type="course">Edit</button> <button class="delete-btn" data-id="${course.id}" data-type="course">Delete</button>` : ''}
      </td>
    </tr>
  `).join('');
}

function renderEnrollmentTable() {
  const body = document.getElementById('enrollmentTableBody');
  body.innerHTML = state.enrollments.map(enrollment => {
    const student = state.students.find(item => item.id === enrollment.student?.id) || {};
    const course = state.courses.find(item => item.id === enrollment.course?.id) || {};
    const statusClass = (enrollment.status || '').toLowerCase() === 'completed' ? 'status-completed' : 'status-enrolled';

    const studentName = student.name || enrollment.student?.name || enrollment.student?.email || 'N/A';
    const courseCode = course.code || enrollment.course?.code || 'N/A';

    return `
      <tr>
        <td>${enrollment.id}</td>
        <td>${studentName}</td>
        <td>${courseCode}</td>
        <td>${enrollment.semester}</td>
        <td><span class="status-pill ${statusClass}">${enrollment.status}</span></td>
        <td>${state.currentRole === 'ADMIN' ? `<button class="delete-btn" data-id="${enrollment.id}" data-type="enrollment">Delete</button>` : ''}</td>
      </tr>
    `;
  }).join('');
}

function populateStudentSelect() {
  const select = document.querySelector('select[name="studentId"]');
  const options = ['<option value="">Select student</option>'];
  state.students.forEach(student => {
    options.push(`<option value="${student.id}">${student.name} (${student.email})</option>`);
  });
  select.innerHTML = options.join('');

  // If a student user is logged in, try to auto-select their profile
  if (state.currentRole === 'STUDENT' && state.currentUser) {
    const found = state.students.find(s => String(s.username || s.email).toLowerCase() === String(state.currentUser).toLowerCase());
    if (found) {
      select.value = found.id;
      // hide manual name/email inputs
      document.getElementById('studentSelectLabel').classList.add('hidden-section');
      document.getElementById('studentNameLabel').classList.add('hidden-section');
      document.getElementById('studentEmailLabel').classList.add('hidden-section');

      // ensure the select exists but keep it in DOM (we use value)
      select.name = 'studentId';
    } else {
      // show name/email so student can add their profile
      document.getElementById('studentSelectLabel').classList.add('hidden-section');
      document.getElementById('studentNameLabel').classList.remove('hidden-section');
      document.getElementById('studentEmailLabel').classList.remove('hidden-section');
      document.getElementById('studentEmailInput').value = '';
    }
  }
}

function populateCourseSelect() {
  const select = document.querySelector('select[name="courseId"]');
  const options = ['<option value="">Select course</option>'];
  state.courses.forEach(course => {
    options.push(`<option value="${course.id}">${course.code} - ${course.title}</option>`);
  });
  select.innerHTML = options.join('');
}

async function refreshAll() {
  try {
    if (!state.currentRole) {
      // nothing to load until logged in
      return;
    }

    await Promise.all([
      loadDashboard(),
      loadStudents(),
      loadCourses(),
      loadEnrollments()
    ]);
  } catch (error) {
    showToast(error.message || 'Unable to load data');
  }
}

document.getElementById('refreshBtn').addEventListener('click', async () => {
  await refreshAll();
});

document.getElementById('loginBtn').addEventListener('click', async () => {
  const username = document.getElementById('usernameInput').value.trim();
  const password = document.getElementById('passwordInput').value.trim();

  if (!username || !password) {
    showToast('Enter username and password');
    return;
  }

  state.currentRole = username === 'admin' ? 'ADMIN' : username === 'student' ? 'STUDENT' : null;
  if (!state.currentRole) {
    showToast('Use admin or student credentials');
    return;
  }

  state.currentUser = username;
  applyRoleAccess();
  try {
    await refreshAll();
    hideLogin();
    showToast(`Logged in as ${username}`);
  } catch (error) {
    showToast(error.message);
  }
});

// Primary login from splash screen
const primaryLoginBtn = document.getElementById('loginPrimaryBtn');
if (primaryLoginBtn) {
  primaryLoginBtn.addEventListener('click', async () => {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    if (!username || !password) {
      showToast('Enter username and password');
      return;
    }

    // mirror into hidden header auth inputs for requests
    document.getElementById('usernameInput').value = username;
    document.getElementById('passwordInput').value = password;

    state.currentRole = username === 'admin' ? 'ADMIN' : username === 'student' ? 'STUDENT' : 'STUDENT';
    // assume non-admin logins are students (server will still enforce credentials)

    state.currentUser = username;
    applyRoleAccess();
    try {
      await refreshAll();
      hideLogin();
      showToast(`Logged in as ${username}`);
    } catch (error) {
      showToast(error.message);
    }
  });
}

// Logout
const logoutBtn = document.getElementById('logoutBtn');
if (logoutBtn) {
  logoutBtn.addEventListener('click', () => {
    showLogin();
    showToast('Logged out');
  });
}

// Profile button (shows simple profile info)
const profileBtn = document.getElementById('profileBtn');
if (profileBtn) {
  profileBtn.addEventListener('click', () => {
    // open a small profile modal or alert for now
    const name = document.getElementById('profileName')?.textContent || state.currentUser;
    showToast(`Signed in as ${name}`);
  });
}

// Signup UI handlers
const showSignupBtn = document.getElementById('showSignupBtn');
const signupBox = document.getElementById('signupBox');
const signupCancelBtn = document.getElementById('signupCancelBtn');
const signupSubmitBtn = document.getElementById('signupSubmitBtn');
if (showSignupBtn && signupBox) {
  showSignupBtn.addEventListener('click', () => signupBox.classList.toggle('hidden-section'));
}
if (signupCancelBtn) signupCancelBtn.addEventListener('click', () => signupBox.classList.add('hidden-section'));
if (signupSubmitBtn) {
  signupSubmitBtn.addEventListener('click', async () => {
    const username = document.getElementById('signupUsername').value.trim();
    const password = document.getElementById('signupPassword').value.trim();
    const name = document.getElementById('signupName').value.trim();
    const email = document.getElementById('signupEmail').value.trim();
    const department = document.getElementById('signupDepartment').value.trim() || 'General';

    if (!username || !password || !name || !email) {
      showToast('Fill all signup fields');
      return;
    }

    try {
      await fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, name, email, department })
      }).then(async res => {
        if (!res.ok) {
          const p = await res.json().catch(() => null);
          throw new Error(p?.error || 'Signup failed');
        }
      });

      // Auto-fill login fields and login
      document.getElementById('loginUsername').value = username;
      document.getElementById('loginPassword').value = password;
      signupBox.classList.add('hidden-section');
      showToast('Account created — please login');
    } catch (err) {
      showToast(err.message || 'Signup failed');
    }
  });
}

document.getElementById('studentForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  if (state.currentRole !== 'ADMIN') {
    showToast('Only admin can manage students');
    return;
  }

  const formData = new FormData(event.target);
  const payload = {
    name: formData.get('name'),
    email: formData.get('email'),
    department: formData.get('department')
  };

  try {
    await api.post('/api/students', payload);
    event.target.reset();
    showToast('Student saved successfully');
    await refreshAll();
  } catch (error) {
    showToast(error.message);
  }
});

document.getElementById('courseForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  if (state.currentRole !== 'ADMIN') {
    showToast('Only admin can add or update courses');
    return;
  }

  const formData = new FormData(event.target);
  const courseId = formData.get('courseId');
  const payload = {
    code: formData.get('code'),
    title: formData.get('title'),
    department: formData.get('department'),
    credits: Number(formData.get('credits')),
    capacity: Number(formData.get('capacity')),
    schedule: formData.get('schedule'),
    prerequisites: formData.get('prerequisites'),
    description: formData.get('description')
  };

  try {
    if (courseId) {
      // Update existing course
      await fetch(`/api/courses/${courseId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
        body: JSON.stringify(payload),
        credentials: 'same-origin'
      }).then(async res => {
        if (!res.ok) {
          const p = await res.json().catch(() => null);
          throw new Error(p?.error || p?.message || 'Update failed');
        }
      });
      showToast('Course updated successfully');
    } else {
      // Create new course
      await api.post('/api/courses', payload);
      showToast('Course saved successfully');
    }

    event.target.reset();
    state.editingCourseId = null;
    document.getElementById('courseFormTitle').textContent = 'Add Course';
    document.getElementById('courseSubmitBtn').textContent = 'Save Course';
    document.getElementById('courseCancelBtn').classList.add('hidden-section');
    await refreshAll();
  } catch (error) {
    showToast(error.message);
  }
});

document.getElementById('enrollmentForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  if (state.currentRole !== 'STUDENT') {
    showToast('Student role is required to enroll');
    return;
  }

  const form = event.target;
  const formData = new FormData(form);
  let studentId = formData.get('studentId');

  try {
    if (!studentId || studentId === '') {
      // create student profile first using studentName/studentEmail
      const name = formData.get('studentName') || document.getElementById('studentNameInput').value;
      const email = formData.get('studentEmail') || document.getElementById('studentEmailInput').value || state.currentUser;
      const department = formData.get('department') || 'General';

      if (!name || !email) {
        showToast('Provide your name and email to create a student profile');
        return;
      }

      const studentPayload = { name, email, department, username: state.currentUser };
      const created = await api.post('/api/students', studentPayload);
      studentId = created.id;
    }

    const payload = {
      studentId: Number(studentId),
      courseId: Number(formData.get('courseId')),
      semester: formData.get('semester'),
      status: formData.get('status')
    };

    await api.post('/api/enrollments', payload);
    form.reset();
    showToast('Enrollment created successfully');
    await refreshAll();
  } catch (error) {
    showToast(error.message);
  }
});

document.addEventListener('click', async (event) => {
  // Edit action (admin only)
  const editBtn = event.target.closest('.edit-btn');
  if (editBtn) {
    const id = editBtn.dataset.id;
    if (state.currentRole !== 'ADMIN') {
      showToast('Only admin can edit courses');
      return;
    }

    const course = state.courses.find(c => String(c.id) === String(id));
    if (!course) {
      showToast('Course not found');
      return;
    }

    // Populate form for editing
    const form = document.getElementById('courseForm');
    form.courseId.value = course.id;
    form.code.value = course.code || '';
    form.title.value = course.title || '';
    form.department.value = course.department || '';
    form.credits.value = course.credits || 3;
    form.capacity.value = course.capacity || 30;
    form.schedule.value = course.schedule || '';
    form.prerequisites.value = course.prerequisites || '';
    form.description.value = course.description || '';

    state.editingCourseId = course.id;
    document.getElementById('courseFormTitle').textContent = 'Edit Course';
    document.getElementById('courseSubmitBtn').textContent = 'Update Course';
    document.getElementById('courseCancelBtn').classList.remove('hidden-section');
    return;
  }

  // Delete action
  const deleteBtn = event.target.closest('.delete-btn');
  if (deleteBtn) {
    const { id, type } = deleteBtn.dataset;
    const url = type === 'student'
      ? `/api/students/${id}`
      : type === 'course'
        ? `/api/courses/${id}`
        : `/api/enrollments/${id}`;

    if (!confirm('Are you sure you want to delete this record?')) {
      return;
    }

    try {
      await api.delete(url);
      showToast('Record deleted');
      await refreshAll();
    } catch (error) {
      showToast(error.message);
    }
  }
});

// Cancel edit button
document.getElementById('courseCancelBtn').addEventListener('click', (event) => {
  const form = document.getElementById('courseForm');
  form.reset();
  form.courseId.value = '';
  state.editingCourseId = null;
  document.getElementById('courseFormTitle').textContent = 'Add Course';
  document.getElementById('courseSubmitBtn').textContent = 'Save Course';
  document.getElementById('courseCancelBtn').classList.add('hidden-section');
});

// Show login screen initially
showLogin();
