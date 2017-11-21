# -*- coding: utf-8 -*-
# Generated by Django 1.11.7 on 2017-11-20 20:50
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='OnOff',
            fields=[
                ('port', models.IntegerField(primary_key=True, serialize=False)),
                ('name', models.CharField(default='Light', max_length=50)),
                ('is_on', models.BooleanField(default=False)),
                ('timer', models.IntegerField(default=0)),
            ],
        ),
    ]
