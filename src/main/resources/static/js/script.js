// console.log('Hello from script.js');
function deleteContact(cId) {
    Swal.fire({
        title: "Are you sure?",
        text: "You won't be able to revert this!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Yes, delete it!"
    }).then((result) => {  // Corrected this line
        if (result.isConfirmed) {  // Corrected this line
            // Swal.fire({
            //   title: "Deleted!",
            //   text: "Your file has been deleted.",
            //   icon: "success"
            // });
            window.location = "/user/delete-contact/" + cId;
        } else {
            Swal.fire({
                title: "Cancelled",
                text: "Your file is safe :)",
                icon: "error"
            });
        }
    });
}
const toggleSidebar = () => {
    if ($('.sidebar').is(':visible')) {
        $('.sidebar').css('display', 'none');
        $('.content').css('margin-left', '0');
        $('.menuBtn').css('display', 'block');
    } else {
        $('.sidebar').css('display', 'block');
        $('.content').css('margin-left', '15%');
        $('.menuBtn').css('display', 'none');
    }
}
const search = () => {
    // console.log('search');
    let query = $("#search-input").val();
    if (query == '') {
        $("#search-result").hide();
    } else {
        console.log(query);
        $("#search-result").show();
        // send request to server
        let url = `http://localhost:8080/search/${query}`;
        fetch(url)
            .then((response) => {
                // console.log(response);
                return response.json();
            })
            .then((data) => {
                let text = `<div class="list-group">`;
                data.forEach((contact) => {
                    text += `<a href="/user/${contact.cid}/contact" class="list-group-item list-group-item-action">${contact.name + ' ' + contact.secondName}</a>`;
                });
                text += `</div>`;
                $("#search-result").html(text);
                $("#search-result").show();
                console.log(data);
            });
    }
}
// const paymentstart = async () => {
//     console.log('payment started');
//     let amount = $("#paymentfield").val()*100;
//     console.log(amount);
//     if (amount == '' || amount == null || amount == 0) {
//         alert('Amount cannot be empty or zero');
//         return;
//     }

//     try {
//         const response = await fetch('/api/payment', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify({ amount: amount })
//         });

//         const data = await response.json();
//         console.log(data);

//         if (data.clientSecret) {
//             const stripe = Stripe('pk_test'); // Replace with your Stripe publishable key
//             const { error } = await stripe.confirmCardPayment(data.clientSecret, {
//                 payment_method: {
//                     type: 'card',
//                     card:{
//                         number: '4242424242424242',
//                         exp_month: 12,
//                         exp_year: 2023,
//                         cvc: '123',
//                     },
//                     billing_details: {
//                         name: 'Customer Name'
//                     }
                    
//                 }
//             });

//             if (error) {
//                 console.error(error.message);
//                 // Handle error
//             } else {
//                 console.log('Payment successful');
//                 // Handle success
                
//             }
//         } else {
//             console.error(data.error);
//             // Handle error
//         }
//     } catch (error) {
//         console.error('Error:', error);
//         // Handle network or other errors
//     }
// };


