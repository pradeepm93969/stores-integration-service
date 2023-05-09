function init() {
	changeTitle();
	
	$("#forgotPasswordForm").validate({
		rules: {
			email: {
				email: true
			},
			mobileNumber: {
				minlength: 9
			},
			password: {
				required: true,
				minlength: 5
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
				minlength: "Please enter valid Password at least 5 charactes"
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

function handleForgotPassword() {
	
	if (!$("#forgotPasswordForm").valid())
		return;

    $('#loader').show(0);
	$.ajax({
		type : "POST",
		url : "/oauth2/resetPassword",
		data : JSON
				.stringify(getFormData($("form[name='forgotPasswordForm']"))),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			/* Authorization header */
			xhr.setRequestHeader("Authorization", basicAuth);
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			displayAlertPopup('fa-check-circle', 'text-green-600', 'Reset Password link has been sent to your email.', function() {
				loadPage('/public/login');
				$("#alertPopup").toggleClass('hidden');
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}
	