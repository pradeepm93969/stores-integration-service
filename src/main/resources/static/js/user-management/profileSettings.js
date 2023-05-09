function init() {
	
	changeTitle();
	
	$("#updateProfileForm").validate({
		rules: {
			firstName: {
				required: true,
				maxlength: 100
			},
			lastName: {
				required: true,
				maxlength: 100
			}
		},
		messages: {
			firstName: {
				required: "Please enter the first name",
				maxlength: "first name cannot be more than 100 characters"
			},
			lastName: {
				required: "Please enter the last name",
				maxlength: "last name cannot be more than 100 characters"
			}
		}
	});
	
    $('#loader1').show(0);
    $.ajax({
           type: "GET",
           url: "/api/currentUser",
           dataType: "json",
           contentType: "application/json; charset=utf-8",
           beforeSend: function (xhr) {
               xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('token'));
               xhr.setRequestHeader("X-Mobile", "false");
           },
           success: function (data) {
          	  $('#chooseAvatar').attr('src', 'img/profileAvatars/'+data.avatarImage);
          	  $('#firstName').val(data.firstName);
          	  $('#lastName').val(data.lastName);
          	  $('#avatarImage').val(data.avatarImage);
          	  $('#loader1').hide(0);
           },
           error: function (data) {
          	  $('#loader1').hide(0);
          	  handleApplicationError(data);
           }
      });
	
}

function changeAvatarImage(img) {
	$('#chooseAvatar').attr('src',$(img).children('img').attr('src'));
	$('#avatarImage').val($(img).children('img').attr('src').split("/")[2]);
	$('#mainPopup').toggleClass('hidden');
}

function chooseAvatarImage() {
	$('#loader').show(0);
	$('#mainModal').html($('.choose-avatar-container').html());
	$('#mainPopup').toggleClass('hidden');
	$('#loader').hide(0);
}

function handleProfileUpdate() {
	
	if (!$("#profileUpdateForm").valid())
		return;
	
      $('#loader1').show(0);
      $.ajax({
             type: "PUT",
             url: "/api/currentUser/updateProfile",
             data: JSON.stringify(getFormData($("form[name=profileUpdateForm]"))),
             dataType: "json",
             contentType: "application/json; charset=utf-8",
             beforeSend: function (xhr) {
                 /* Authorization header */
                 xhr.setRequestHeader("Authorization", "Bearer "+ localStorage.getItem('token'));
                 xhr.setRequestHeader("X-Mobile", "false");
             },
             success: function (data) {
	       		localStorage.setItem('firstName',data.firstName);
	        	localStorage.setItem('avatar',data.avatarImage);
	        	$('#headerFirstName').text(data.firstName);
	        	$('#headerAvatar').attr('src', 'img/profileAvatars/' + data.avatarImage);
	        	$('#loader1').hide(0);
	        	displayAlertPopup('fa-check-circle', 'text-green-600', 'Profile saved successfully.', function() {
					$('#alertPopup').toggleClass('hidden');
				});
             },
             error: function (data) {
            	 $('#loader').hide(0);
            	 handleApplicationError(data);
             }
      });
}
