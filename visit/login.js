	var selectedFile;
	var user;
	var uid;
	var c=0;
	var useremail;

	firebase.auth().onAuthStateChanged(function(user) {
		if (user) {
          // User is signed in.
           useremail = user.email;
           uid = user.uid;
          //alert(uid);
      }
	});
 	
 	function togglelink(){
 		if(c==1){
			firebase.auth().signOut();
			document.getElementById('signinbt').innerHTML="Sign in";
			document.getElementById('signupbt').style.display="block";
			document.getElementById('btclose').click();
			c=0;
 		}
 	}

	function handleSignUp(){

      var email = document.getElementById('inputEmail').value;
      var password = document.getElementById('inputSignupPassword').value;
      if (email.length < 4) {
        alert('Please enter an email address.');
        return;
      }
      if (password.length < 4) {
        alert('Please enter a password.');
        return;
      }
      // Sign in with email and pass.
      // [START createwithemail]
      firebase.auth().createUserWithEmailAndPassword(email, password).catch(function(error) {
        // Handle Errors here.
       
        var errorCode = error.code;
        var errorMessage = error.message;
        // [START_EXCLUDE]
        if (errorCode == 'auth/weak-password') {
          alert('The password is too weak.');
        } else {
          alert(errorMessage);
        }
        console.log(error);
        // [END_EXCLUDE]
      });
      document.getElementById('signinbt').innerHTML="Sign out";
		document.getElementById('signupbt').style.display="none";
		c=1;
		document.getElementById('upbtclose').click();
	}

	function handlesignin(){
		var cuser=firebase.auth().currentUser;

		if(cuser==null){
			
		 var email = document.getElementById('signinemail').value;
        var password = document.getElementById('inputSigninPassword').value;
        if (email.length < 4) {
          alert('Please enter an email address.');
          return;
        }
        if (password.length < 4) {
          alert('Please enter a password.');
          return;
        }
        // Sign in with email and pass.
        // [START authwithemail]
        firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error) {
          // Handle Errors here.
          var errorCode = error.code;
          var errorMessage = error.message;
          // [START_EXCLUDE]
          if (errorCode === 'auth/wrong-password') {
            alert('Wrong password.');
          } else {
            alert(errorMessage);
          }
          console.log(error);
          // [END_EXCLUDE]
        });
        // [END authwithemail]
		document.getElementById('signinbt').innerHTML="Sign out";
		document.getElementById('signupbt').style.display="none";
		c=1;
		document.getElementById('btclose').click();

		}
		else{
			
			firebase.auth().signOut();
			document.getElementById('signinbt').innerHTML="Sign in";
			c=0;
		}
	}
    
    

    $('#upload').on("change", function(event){
    	selectedFile= event.target.files[0];
    	//alert(selectedFile.name);
    })

    function confirmUpload(){
    	
    	var filename = uid+selectedFile.name;
    	var storageRef=firebase.storage().ref('/floorplans/'+useremail);
    	var uploadTask=storageRef.child(selectedFile.name).put(selectedFile);
    	var lat=document.getElementById('latitude').value;
    	var long=document.getElementById('longitude').value;
    	//alert(lat);
    	uploadTask.on('state_changed', function(snapshot){
		  // Observe state change events such as progress, pause, and resume
		  // Get task progress, including the number of bytes uploaded and the total number of bytes to be uploaded
		  var progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
		  //alert('Upload is ' + progress + '% done');
		  switch (snapshot.state) {
		    case firebase.storage.TaskState.PAUSED: // or 'paused'
		      //alert('Upload is paused');
		      break;
		    case firebase.storage.TaskState.RUNNING: // or 'running'
		      //alert('Upload is running');
		      break;
		  }
		}, function(error) {
		  // Handle unsuccessful uploads
		}, function() {
		  // Handle successful uploads on complete
		  // For instance, get the download URL: https://firebasestorage.googleapis.com/...
		  uploadTask.snapshot.ref.getDownloadURL().then(function(downloadURL) {
		    //alert('File available at', downloadURL);
		    firebase.database().ref('Users/' + uid).set({
		    	Email: useremail,
				Floorplan: downloadURL,
				Latitude: lat,
				Longitude: long
			});

		  });
		});
    	
    }