function init() {
	changeTitle();
}

function handleDownloadUser() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/user/extract?" + $("form[name=searchUserForm]").serialize(),
		beforeSend : function(xhr) {
			/* Authorization header */
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			var blob=new Blob([data]);
		    var link=document.createElement('a');
		    link.href=window.URL.createObjectURL(blob);
		    link.download="User_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchUser() {
	
	var searchData = "pageSize=" + $("#userTableSection #pageSize").val() + "&pageNo=" + $("#userTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/user?" + searchData + $("form[name=searchUserForm]").serialize(),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#userTableSection').hasClass('hidden')) {
				$('#userTableSection').toggleClass('hidden');
			}
			$('#userTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#userTableSection #selectAll').prop('checked',false);
				var paramsArray = ["id","enabled","firstName","lastName","email","mobileNumberCountryCode","mobileNumber","avatarImage","invalidLoginAttempts","invalidLoginAt"];
				$('#userTableSection table tbody').html(constructTableRows(true, true, true, false, 'User', data, paramsArray));
				constructPagination('userTableSection', $("#userTableSection #pageSize").val(), $("#userTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteUser(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/user/" + id,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			beforeSend : function(xhr) {
				xhr.setRequestHeader("Authorization", "Bearer "
						+ localStorage.getItem('token'));
				xhr.setRequestHeader("X-Mobile", "false");
			},
			success : function(data) {
				$('#loader1').hide(0);
				displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully deleted...', function() {
					$('#alertPopup').toggleClass('hidden');
					handleSearchUser();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateUser(id) {
	$('#loader').show(0);
	$('#mainModal').html($('.update-user-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	
	$.ajax({
		type : "GET",
		url : "/api/role",
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		async: false,
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			var html = "";
			for (var i = 0; i < data.content.length; i++) {
				html += '<div class="roles">';
				html += '<input class="cursor-pointer" type="checkbox" id="' + data.content[i].id + '"/>';
				html += '<span class="ml-2">' + data.content[i].name + '</span>';
				html += '</div>';
			}
			$('#mainModal #rolesMap').html(html);
			$('#loader').hide(0);
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
	
	$.ajax({
		type : "GET",
		url : "/api/user/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			
			loadFormData($('#mainModal form[name=updateUserForm]'), data);
			//load Country Code
			$('#mainModal form[name=updateUserForm] input[name=mobileNumberCountryCode]').val(data.mobileNumberCountryCode);
			$('#mainModal form[name=updateUserForm] #__countryCode').html(data.mobileNumberCountryCode);
			
			var countryCodeFlag = "ae";
			for (var i = 0; i < allCountries.length; i++) {
				if (Number((allCountries[i]).dialCode) === Number(data.mobileNumberCountryCode)) {
					countryCodeFlag = allCountries[i].iso2;
				}
			}
			$("#intlTelInput img").attr('src','/img/flags/32/'+countryCodeFlag+'.png');
			
			console.log(JSON.stringify(data.roles));
			for (var i = 0; i < data.roles.length; i++) {
				$('#mainModal form[name=updateUserForm] #' +  data.roles[i].id).prop( "checked", true );
			}
			$('#loader').hide(0);
			
			$("#mainModal form[name=updateUserForm]").validate({
				rules: {
					username: {
						required: true,
						email: true
					},
					email: {
						equalTo: "#username"
					},
					mobileNumber: {
						required: true,
						minlength: 9
					},
					firstName: {
						required: true,
						minlength: 3
					},
					lastName: {
						required: true,
						minlength: 3
					}
				},
				messages: {
					username: {
					},
					email: {
						equalTo: "Must be same as username"
					},
					mobileNumber: {
						minlength: "Minimum 9 numbers required"
					},
					firstName: {
						minlength: "Minimum 3 characters required"
					},
					lastName: {
						minlength: "Minimum 3 characters required"
					}
					
				}
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
	
	
}

function handleUpdateUser() {
	
	if (!$("#mainModal form[name=updateUserForm]").valid())
		return;
	
	$('#loader').show(0);
	
	var userData = getFormData($("#mainModal form[name=updateUserForm]"));
	userData["roleInput"]=buildRoleMap($("#mainModal form[name=updateUserForm]"));
	
	$.ajax({
		type : "PUT",
		url : "/api/user/" + userData.id,
		data: JSON.stringify(userData),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully updated...', function() {
				$('#alertPopup').toggleClass('hidden');
				handleSearchUser();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function createUser() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-user-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	
	$.ajax({
		type : "GET",
		url : "/api/role",
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			var html = "";
			for (var i = 0; i < data.content.length; i++) {
				html += '<div class="roles">';
				html += '<input class="cursor-pointer" type="checkbox" id="' + data.content[i].id + '"/>';
				html += '<span class="ml-2">' + data.content[i].name + '</span>';
				html += '</div>';
			}
			$('#mainModal #rolesMap').html(html);
			$('#loader').hide(0);
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
	
	$('#loader').hide(0);
	
	$("#mainModal form[name=createUserForm]").validate({
		rules: {
			username: {
				required: true,
				email: true
			},
			password: {
				required: true,
				minlength: 8,
				checkLower: true,
			    checkUpper: true,
			    checkDigit: true,
			    checkSpecial: true
			},
			email: {
				equalTo: "#username"
			},
			mobileNumber: {
				required: true,
				minlength: 9
			},
			firstName: {
				required: true,
				minlength: 3
			},
			lastName: {
				required: true,
				minlength: 3
			}
		},
		messages: {
			username: {
				
			},
			password: {
				required: "Please enter the password",
				minlength: "Password must be at least 8 characters",
				checkLower: "Password must be at least 1 lower case characters",
			    checkUpper: "Password must be at least 1 upper case characters",
			    checkDigit: "Password must be at least 1 digit",
			    checkSpecial: "Password must contain atleast 1 special letter(*,@,#,$,&,_,-)"
			},
			email: {
				equalTo: "Must be same as username"
			},
			mobileNumber: {
				minlength: "Minimum 9 numbers required"
			},
			firstName: {
				minlength: "Minimum 3 characters required"
			},
			lastName: {
				minlength: "Minimum 3 characters required"
			}
			
		}
	});
}

function handleCreateUser() {
	if (!$("#mainModal form[name=createUserForm]").valid())
		return;
	
	$('#loader').show(0);
	
	var userData = getFormData($("#mainModal form[name=createUserForm]"));
	userData["roleInput"]=buildRoleMap($("#mainModal form[name=createUserForm]"));
	$.ajax({
		type : "POST",
		url : "/api/user/",
		data: JSON.stringify(userData),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully created...', function() {
				$('#alertPopup').toggleClass('hidden');
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}

function buildRoleMap(form) {
	var roles = []
	var rolesMap = $(form).find('#rolesMap');
	var cards = $(rolesMap).find('.roles');
	var card = null;
	var roleIndex = 0;
	for (var i = 0; i < cards.length; i++) {
		card = cards[i];
		if ($(card).find('input').is(':checked')) {
			roles[roleIndex++] = $(card).find('input').attr('id');
		}
	}
	return roles;
}

