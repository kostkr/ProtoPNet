##### MODEL AND DATA LOADING
import torch
import torch.utils.data
import torchvision.transforms as transforms
from torch.autograd import Variable
import numpy as np
from PIL import Image

import re

import os

from preprocess import mean, std

import argparse

parser = argparse.ArgumentParser()
parser.add_argument('-gpuid', nargs=1, type=str, default='0')
parser.add_argument('-modeldir', nargs=1, type=str)
parser.add_argument('-model', nargs=1, type=str)
parser.add_argument('-imgpath', nargs=1, type=str)
#parser.add_argument('-imgclass', nargs=1, type=int, default=-1)
args = parser.parse_args()

os.environ['CUDA_VISIBLE_DEVICES'] = args.gpuid[0]

# specify the test image to be analyzed
test_image_label = 15 #15

test_image_path = args.imgpath[0]

# load the model
check_test_accu = False

load_model_dir = args.modeldir[0] #'./saved_models/vgg19/003/'
load_model_name = args.model[0] #'10_18push0.7822.pth'

load_model_path = os.path.join(load_model_dir, load_model_name)
epoch_number_str = re.search(r'\d+', load_model_name).group(0)
start_epoch_number = int(epoch_number_str)

# new code
device = torch.device('cuda:' + args.gpuid[0] if torch.cuda.is_available() else 'cpu')

#ppnet = torch.load(load_model_path)
ppnet = torch.load(load_model_path, map_location=device)
#ppnet = ppnet.cuda()
ppnet = ppnet.to(device)
ppnet_multi = torch.nn.DataParallel(ppnet)

img_size = ppnet_multi.module.img_size
prototype_shape = ppnet.prototype_shape
max_dist = prototype_shape[1] * prototype_shape[2] * prototype_shape[3]

class_specific = True

normalize = transforms.Normalize(mean=mean,
                                 std=std)

##### SANITY CHECK
# confirm prototype class identity
load_img_dir = os.path.join(load_model_dir, 'img')

prototype_info = np.load(os.path.join(load_img_dir, 'epoch-'+epoch_number_str, 'bb'+epoch_number_str+'.npy'))
prototype_img_identity = prototype_info[:, -1]

# confirm prototype connects most strongly to its own class
prototype_max_connection = torch.argmax(ppnet.last_layer.weight, dim=0)
prototype_max_connection = prototype_max_connection.cpu().numpy()

# load the test image and forward it through the network
preprocess = transforms.Compose([
    transforms.Resize((img_size,img_size)),
    transforms.ToTensor(),
    normalize
])

img_pil = Image.open(test_image_path)
img_tensor = preprocess(img_pil)
img_variable = Variable(img_tensor.unsqueeze(0))

images_test = img_variable.to(device)
labels_test = torch.tensor([test_image_label]).to(device)

logits, min_distances = ppnet_multi(images_test)
conv_output, distances = ppnet.push_forward(images_test)
prototype_activations = ppnet.distance_2_similarity(min_distances)
prototype_activation_patterns = ppnet.distance_2_similarity(distances)
if ppnet.prototype_activation_function == 'linear':
    prototype_activations = prototype_activations + max_dist
    prototype_activation_patterns = prototype_activation_patterns + max_dist

tables = []
for i in range(logits.size(0)):
    tables.append((torch.argmax(logits, dim=1)[i].item(), labels_test[i].item()))

idx = 0
predicted_cls = tables[idx][0]
correct_cls = tables[idx][1]

##### MOST ACTIVATED (NEAREST) 10 PROTOTYPES OF THIS IMAGE
array_act, sorted_indices_act = torch.sort(prototype_activations[idx])
for i in range(1,11):
    image_path = os.path.join(load_img_dir, 'epoch-'+str(start_epoch_number), 'prototype-img-original'+str(sorted_indices_act[-i].item())+'.png')

    print(prototype_img_identity[sorted_indices_act[-i].item()])# prototype class identity
    print(image_path) #image path
    print('prototype index: {0}'.format(sorted_indices_act[-i].item()))
    print('activation value (similarity score): {0}'.format(array_act[-i]))
    print('last layer connection with predicted class: {0}'.format(ppnet.last_layer.weight[predicted_cls][sorted_indices_act[-i].item()]))