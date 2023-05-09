function init() {
	
	changeTitle();
	
	if (localStorage.getItem('avatar') != null && localStorage.getItem('avatar') != '') {
		$('#loginAvatar').removeClass('hidden');
		$('#loginAvatar').attr('src','img/profileAvatars/'+localStorage.getItem('avatar'));
	}
	
	if (localStorage.getItem('firstName') != null && localStorage.getItem('firstName') != '') {
		$('#logged-in-message').removeClass('hidden');
		$('#loginFirstName').text(localStorage.getItem('firstName'));
	} else {
		$('#non-logged-in-message').removeClass('hidden');
	}
	
	$("#loginForm").validate({
		rules: {
			email: {
				email: true
			},
			mobileNumber: {
				minlength: 9
			},
			password: {
				required: true,
				minlength: 8
			}
		},
		messages: {
			email: {
				
			},
			mobileNumber: {
				minlength: "Please enter valid mobile number at least 9 digits"
			},
			password: {
				required: "Please enter valid Password",
				minlength: "Please enter valid Password at least 8 charactes"
			}
		}
	});

    $(document).ready(function () {
	    $('#emailBox input[name=email]').on('input', function() {
	        if (isNaN($('#emailBox input[name=email]').val()) == false) {
	           $('#mobileBox').removeClass('hidden');
	           $('#mobileBox input[name=mobileNumber]').val($('#emailBox input[name=email]').val());
	           $('#emailBox').addClass('hidden');
	           $('#emailBox input[name=email]').val('');
	           $('#mobileBox input[name=mobileNumber]').focus();
	           $('#emailBox input[name=email]').removeAttr('required');
	           $('#mobileBox input[name=mobileNumber]').attr('required',true);
	        }
	    });
	    $('#mobileBox input').on('input', function() {
	        if (isNaN($('#mobileBox input[name=mobileNumber]').val()) == true) {
	           $('#emailBox').removeClass('hidden');
	           $('#emailBox [name=email]').val($('#mobileBox input[name=mobileNumber]').val());
	           $('#mobileBox').addClass('hidden');
	           $('#mobileBox input[name=mobileNumber]').val('');
	           $('#emailBox [name=email]').focus();
	           $('#mobileBox input[name=mobileNumber]').removeAttr('required');
	           $('#emailBox [name=email]').attr('required',true);
	        }
	    });
    }); 
    
}

function handleLogin() {
	
	if (!$("#loginForm").valid())
		return;
	
	$('#loader').show(0);
	$.ajax({
		type : "POST",
		url : "/oauth2/login",
		data : JSON.stringify(getFormData($("form[name='loginForm']"))),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			/* Authorization header */
			xhr.setRequestHeader("Authorization", basicAuth);
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			localStorage.setItem('token', data.accessToken);
			localStorage.setItem('firstName', data.firstName);
			localStorage.setItem('avatar', data.avatarImage);
			$('#loader').hide(0);
			window.location.href = "/home";
		},
		error : function(data) {
			console.log(data);
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}
	