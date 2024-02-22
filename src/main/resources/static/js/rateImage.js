function showLoadingSpinner() {
    const imageFrame = document.getElementById('imageFrame');
    imageFrame.innerHTML = '<p>Loading...</p>';
}

async function fetchImages() {
    showLoadingSpinner();
    const response = await fetch('/api/getImages');
    return await response.json();
}

async function rateImage(name, details, rating) {
    // sent rate
    await uploadRating({name, details, rating});
}

async function uploadRating(data) {
    await fetch('/api/rateImage', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    });
}

function showNextImages(images) {
    const imageFrame = document.getElementById('imageFrame');
    imageFrame.innerHTML = '';

    for (let i = 0; i < images.length; i++) {
        const container = document.createElement('div');
        container.classList.add('image-container');

        const imgElement = document.createElement('img');
        imgElement.src = `data:image/jpeg;base64,${images[i].image}`;

        const buttonContainer = document.createElement('div');
        buttonContainer.classList.add('button-container');

        const label = document.createElement('p');

        // Add a button for detailed information only for images with index > 0
        if (i > 0) {
            label.textContent = `Top ${i} - ${images[i].name}`;
            label.style.fontWeight = 'bold';
            label.style.marginBottom = '5px';

            const infoButton = document.createElement('button');
            infoButton.textContent = 'Details';
            infoButton.classList.add('btn', 'btn-info');
            infoButton.addEventListener('click', () => toggleDetails(event, images[i].details));

            // Add a button for 'Correct'
            const correctButton = document.createElement('button');
            correctButton.textContent = 'Correct';
            correctButton.classList.add('btn', 'btn-success');
            correctButton.addEventListener('click', () => {
                rateImage(images[i].name, images[i].details, 'correct');
                container.classList.add('border-correct');
            });

            // Add a button for 'Incorrect'
            const incorrectButton = document.createElement('button');
            incorrectButton.textContent = 'Incorrect';
            incorrectButton.classList.add('btn', 'btn-warning');
            incorrectButton.addEventListener('click', () => {
                rateImage(images[i].name, images[i].details, 'incorrect');
                container.classList.add('border-incorrect');
            });

            buttonContainer.appendChild(correctButton);
            buttonContainer.appendChild(incorrectButton);
            buttonContainer.appendChild(infoButton);
        } else {
            // Add "original" label to the first image
            label.textContent = `Original image - ${images[i].name}`;
            label.style.fontWeight = 'bold';
            label.style.marginBottom = '5px';
        }

        container.appendChild(label)
        container.appendChild(imgElement);
        container.appendChild(buttonContainer);
        imageFrame.appendChild(container);


    }
}

function toggleDetails(event, details) {
    const button = event.currentTarget;
    button.classList.toggle('active');

    const imgContainer = button.parentElement.parentElement;
    const infoContainer = imgContainer.querySelector('.details-info');

    if (button.classList.contains('active')) {
        // If the button is active, show information
        if (!infoContainer) {
            // Create a div for information if it doesn't exist
            const detailsInfo = document.createElement('div');
            detailsInfo.classList.add('details-info');

            const detailsArray = details.split('\n');
            for (let i = 0; i < detailsArray.length; ++i) {
                const lineDiv = document.createElement('div');
                lineDiv.textContent = detailsArray[i];
                detailsInfo.appendChild(lineDiv);
            }

            imgContainer.appendChild(detailsInfo);
        }
    } else {
        // If the button is not active, hide the information
        if (infoContainer) {
            imgContainer.removeChild(infoContainer);
        }
    }
}

function showNextImagesButton() {
    fetchImages().then(images => showNextImages(images));
}

// initialize first image
showNextImagesButton()