const api = 'http://localhost:8000';

function login() {
    let email = document.getElementById('email').value;
    let password = document.getElementById('password').value;
    if (email.toString().trim() === '' || password.toString().trim() === '') {
        Swal.fire({
            position: 'center',
            icon: 'error',
            title: 'The fields should not be empty !',
            showConfirmButton: false,
            timer: 2500
        });
    } else {
        axios.post(api + '/client/login', {
            email: email,
            password: password,
        }).then(function (response) {
            if (response.data.code === '1') {
                Swal.fire({
                    position: 'center',
                    icon: 'success',
                    title: 'Logged in successfully !',
                    showConfirmButton: false,
                    timer: 2500
                });
                cookie.set('token', response.data.message);
                cookie.set('type', 'client');
                window.location.href = "index.html";
            } else {
                axios.post(api + '/admin/login', {
                    email: email,
                    password: password,
                }).then(function (response) {
                    if (response.data.code === '1') {
                        Swal.fire({
                            position: 'center',
                            icon: 'success',
                            title: 'Logged in successfully !',
                            showConfirmButton: false,
                            timer: 2500
                        });
                        cookie.set('token', response.data.message);
                        cookie.set('type', 'admin');
                        window.location.href = "index.html";
                    } else {
                        Swal.fire({
                            position: 'center',
                            icon: 'error',
                            title: response.data.message,
                            showConfirmButton: false,
                            timer: 2500
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            }
        }).catch(function (error) {
            console.log(error);
        });
    }
}

function sendMessage() {
    let name = document.getElementById('name').value;
    let email = document.getElementById('email').value;
    let subject = document.getElementById('subject').value;
    let message = document.getElementById('message').value;
    if (subject.toString().trim() === '' || message.toString().trim() === '' || name.toString().trim() === '' || email.toString().trim() === '') {
        Swal.fire({
            position: 'center',
            icon: 'error',
            title: 'The fields should not be empty !',
            showConfirmButton: false,
            timer: 2500
        });
    } else {
        axios.post(api + '/contact', {
            name: name,
            email: email,
            subject: subject,
            message: message,
        }, {headers: {"Authorization": cookie.get('token')}}).then(function (response) {
            if (response.data.code === '1') {
                Swal.fire({
                    position: 'center',
                    icon: 'success',
                    title: 'Your message has been sent successfully !',
                    showConfirmButton: false,
                    timer: 2500
                });
                window.location.href = "index.html";
            } else {
                Swal.fire({
                    position: 'center',
                    icon: 'error',
                    title: 'An error occurred, please try again later !',
                    showConfirmButton: false,
                    timer: 2500
                });
            }
        }).catch(function (error) {
            console.log(error);
        });
    }
}

function logout() {
    cookie.remove('token');
    window.location.href = "login.html";
}

function register() {
    let firstName = document.getElementById('firstName').value;
    let lastName = document.getElementById('lastName').value;
    let email = document.getElementById('email').value;
    let password = document.getElementById('password').value;
    if (firstName.toString().trim() === '' || lastName.toString().trim() === '' || email.toString().trim() === '' || password.toString().trim() === '') {
        Swal.fire({
            position: 'center',
            icon: 'error',
            title: 'The fields should not be empty !',
            showConfirmButton: false,
            timer: 2500
        });
    } else {
        axios.post(api + '/client/register', {
            firstName: firstName,
            lastName: lastName,
            email: email,
            password: password,
        }).then(function (response) {
            if (response.data.code === '1') {
                Swal.fire({
                    position: 'center',
                    icon: 'success',
                    title: 'Your account has been created successfully, please verify your email address !',
                    showConfirmButton: false,
                    timer: 2500
                });
                window.location.href = "login.html";
            } else {
                Swal.fire({
                    position: 'center',
                    icon: 'error',
                    title: 'An error occurred, please try again later !',
                    showConfirmButton: false,
                    timer: 2500
                });
            }
        }).catch(function (error) {
            console.log(error);
        });
    }
}

function viewFilm(id) {
    axios.get(api + '/films/' + id, {
        headers: {"Authorization": cookie.get('token')}
    }).then(function (response) {
        let link = response.data.link;
        if (link !== undefined) {
            Swal.fire({
                title: '<strong>' + response.data.name + '</strong>',
                width: 800,
                html:
                    '<video width="600" controls>\n' +
                    '  <source src="http://localhost:8000/videos/' + link + '.mp4" type="video/mp4">\n' +
                    '  Your browser does not support HTML video' +
                    '</video><br><center><p>' + response.data.description + '</p></center>',
                showCloseButton: true,
                showConfirmButton: false,
                showCancelButton: false
            });
        }
    }).catch(function (error) {
        console.log(error);
    });
}

function deleteFilm(id) {
    axios.delete(api + '/films/' + id, {
        headers: {"Authorization": cookie.get('token')}
    }).then(function (response) {
        if (response.data.code === '1') {
            Swal.fire({
                position: 'center',
                icon: 'success',
                title: 'The operation has been completed successfully !',
                showConfirmButton: false,
                timer: 2500
            });
            window.location.href = "add.html";
        } else {
            Swal.fire({
                position: 'center',
                icon: 'error',
                title: 'An error occurred, please try again later !',
                showConfirmButton: false,
                timer: 2500
            });
        }
    }).catch(function (error) {
        console.log(error);
    });
}

function getFilms() {
    axios.get(api + '/films', {
        headers: {"Authorization": cookie.get('token')}
    }).then(function (response) {
        for (var i = 0; i < response.data.length; i++) {
            add((i + 1), response.data[i]);
        }
    }).catch(function (error) {
        console.log(error);
    });
}

function add(i, element) {
    let all = document.getElementById('all-films');
    var item = '<div class="col-lg-4 col-md-6 d-flex align-items-stretch">' +
        '<div class="member">' +
        '<img src="http://localhost:8000/photos/' + element.photo + '.jpg" class="img-fluid" alt="">' +
        '<div class="member-content">' +
        '<h4>' + element.name + '</h4>' +
        '<span>' + element.type + '</span>' +
        '<center>' +
        '<div class="text-center">' +
        '<button class="btn" type="button" onclick="viewFilm(' + element.objectId.singleValue + ')">View This Film</button>' +
        '</div></center></div></div></div>';

    all.innerHTML += item;
}

function getAllFilms() {
    axios.get(api + '/films', {
        headers: {"Authorization": cookie.get('token')}
    }).then(function (response) {
        for (var i = 0; i < response.data.length; i++) {
            addFilm((i + 1), response.data[i]);
        }
    }).catch(function (error) {
        console.log(error);
    });
}

function addFilm(i, element) {
    let all = document.getElementById('all-films');
    var item = '<div class="col-lg-4 col-md-6 d-flex align-items-stretch">' +
        '<div class="member">' +
        '<img src="http://localhost:8000/photos/' + element.photo + '.jpg" class="img-fluid" alt="">' +
        '<div class="member-content">' +
        '<h4>' + element.name + '</h4>' +
        '<span>' + element.type + '</span>' +
        '<center>' +
        '<div class="text-center">' +
        '<button class="btn" type="button" onclick="viewFilm(' + element.objectId.singleValue + ')">View This Film</button>' +
        '<button style=" margin-top: 10px; " class="btn" type="button" onclick="deleteFilm(' + element.objectId.singleValue + ')">Delete This Film</button>' +
        '</div></center></div></div></div>';

    all.innerHTML += item;
}

function getBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
}

function addNewFilm() {
    let name = document.getElementById('name').value;
    let description = document.getElementById('description').value;
    let duration = document.getElementById('duration').value;
    let quality = document.getElementById('quality').value;
    let age = document.getElementById('age').value;
    let year = document.getElementById('year').value;
    let rating = document.getElementById('rating').value;
    let type = document.getElementById('type').value;
    var photo = document.querySelector('#filmPhoto > input[type="file"]').files[0];
    var link = document.querySelector('#filmVideo > input[type="file"]').files[0];
    if (name.toString().trim() === '' || type.toString().trim() === '' || description.toString().trim() === '' || duration.toString().trim() === '' || quality.toString().trim() === '') {
        Swal.fire({
            position: 'center',
            icon: 'error',
            title: 'The fields should not be empty !',
            showConfirmButton: false,
            timer: 2500
        });
    } else {
        getBase64(photo).then(
            function (data) {
                getBase64(link).then(
                    function (data2) {
                        axios.post(api + '/films', {
                                name: name,
                                description: description,
                                duration: duration,
                                quality: quality,
                                age: age,
                                year: year,
                                rating: rating,
                                type: type,
                                photo: data,
                                link: data2
                            }, {headers: {"Authorization": cookie.get('token')}}
                        ).then(function (response) {
                                Swal.fire({
                                    position: 'center',
                                    icon: 'success',
                                    title: 'The operation has been completed successfully !',
                                    showConfirmButton: false,
                                    timer: 2500
                                });
                            window.location.href = "add.html";
                        }).catch(function (error) {
                            console.log(error);
                        });
                    }
                );
            }
        );
    }
}
