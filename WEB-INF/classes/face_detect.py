import cv2
import sys
sys.path.append('/usr/local/lib/python3.9/dist-packages')
face_cascade = cv2.CascadeClassifier('/opt/tomcat/webapps/ROOT/WEB-INF/classes/models/matchin/haarcascade_frontalface_default.xml')
# Read the input image
img = cv2.imread(sys.argv[1])
if img is None:
    print("Error: Unable to load image.")
    sys.exit(1)

# Convert into grayscale
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
# Detect faces
faces = face_cascade.detectMultiScale(gray, 1.1, 4)
# Draw rectangle around the faces
for (x, y, w, h) in faces:
    cv2.rectangle(img, (x, y), (x+w, y+h), (255, 0, 0), 2)
# Save the output image with rectangles drawn around the faces
output_filename = sys.argv[1].split('.')[0] + '_output.jpg'
cv2.imwrite(output_filename, img)
print(f"Output image saved as {output_filename}")
