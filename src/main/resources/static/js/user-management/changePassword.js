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
	
}

function handleChangePassword() {
	
	if (!$("#changePasswordForm").valid())
		return;
	
      $('#loader1').show(0);
      $.ajax({
             type: "PUT",
             url: "/api/currentUser/updatePassword",
             data: JSON.stringify(getFormData($("form[name='changePasswordForm']"))),
             dataType: "json",
             contentType: "application/json; charset=utf-8",
             beforeSend: function (xhr) {
                 /* Authorization header */
                 xhr.setRequestHeader("Authorization", "Bearer "+ localStorage.getItem('token'));
                 xhr.setRequestHeader("X-Mobile", "false");
             },
             success: function (data) {
                $('#loader1').hide(0);
                displayAlertPopup('fa-check-circle', 'text-green-600', 'Password saved successfully.', function() {
					$('#alertPopup').toggleClass('hidden');
				});
             },
             error: function (data) {
            	 $('#loader1').hide(0);
            	 handleApplicationError(data);
             }
      });
}
	