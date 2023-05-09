function init() {
	changeTitle();
	
	$("#changePasswordForm").validate({
		rules: {
			newPassword: {
				required: true,
				minlength: 8,
				checkLower: true,
			    checkUpper: true,
			    checkDigit: true,
			    checkSpecial: true
			},
			confirmPassword: {
				equalTo: "#newPassword"
			}
		},
		messages: {
			newPassword: {
				required: "Please enter the password",
				minlength: "Password must be at least 8 characters",
				checkLower: "Password must be at least 1 lower case characters",
			    checkUpper: "Password must be at least 1 upper case characters",
			    checkDigit: "Password must be at least 1 digit",
			    checkSpecial: "Password must contain atleast 1 special letter(*,@,#,$,&,_,-)"
			},
			confirmPassword: {
				equalTo: "Confirm password must be same as password"
			}
		}
	});
	
	var formData = {};
	
    $('#loader').show(0);
    $.ajax({
           type: "POST",
           url: "/oauth2/verifyResetPasswordToken?token=" + getQueryString("token"),
           data: JSON.stringify(formData),
           dataType: "json",
           contentType: "application/json; charset=utf-8",
           beforeSend: function (xhr) {
               xhr.setRequestHeader("Authorization", basicAuth);
               xhr.setRequestHeader("X-Mobile", "false");
           },
           success: function (data) {
              $('#loader').hide(0);
              $('#validToken').removeClass('hidden');
          	  $('#invalidToken').addClass('hidden');
          	  $('#changePasswordAvatar').attr('src', 'img/profileAvatars/'+data.avatarImage);
          	  $('#firstName').text(data.firstName);
          	  $('#newPassword+input').focus();
           },
           error: function (data) {
          	  $('#loader').hide(0);
          	  $('#validToken').addClass('hidden');
          	  $('#invalidToken').removeClass('hidden');
           }
      });
   
}

function handleChangePassword() {
	
	if (!$("#changePasswordForm").valid())
		return;
	
      $('#loader').show(0);
      $.ajax({
             type: "POST",
             url: "/oauth2/changePassword?token=" + getQueryString("token"),
             data: JSON.stringify(getFormData($("form[name='changePasswordForm']"))),
             dataType: "json",
             contentType: "application/json; charset=utf-8",
             beforeSend: function (xhr) {
                 /* Authorization header */
                 xhr.setRequestHeader("Authorization", basicAuth);
                 xhr.setRequestHeader("X-Mobile", "false");
             },
             success: function (data) {
                $('#loader').hide(0);
                displayAlertPopup('fa-check-circle', 'text-green-600', 'Password changed successfully. Please proceed with login.', function() {
    				$("#alertPopup").toggleClass('hidden');
    			});
    			loadPage('/public/login');
             },
             error: function (data) {
            	 $('#loader').hide(0);
            	 handleApplicationError(data);
             }
      });
}
	